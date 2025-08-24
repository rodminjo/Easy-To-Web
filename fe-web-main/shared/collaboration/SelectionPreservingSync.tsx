"use client";

import { useEffect, useRef, useCallback } from "react";
import { useEditor } from "@craftjs/core";
import { useYjs } from "./YjsProvider";
import * as Y from "yjs";

interface SelectionPreservingSyncProps {
  onContentChange?: (content: string) => void;
}

export const SelectionPreservingSync = ({
  onContentChange,
}: SelectionPreservingSyncProps) => {
  const { doc, isConnected, provider } = useYjs();
  const { actions, query } = useEditor();

  // State management refs
  const isApplyingRemoteChange = useRef(false);
  const lastLocalState = useRef<string>("");
  const lastRemoteState = useRef<string>("");
  const preservedSelection = useRef<{
    nodeIds: string[];
    focusedNodeId?: string;
    cursorPosition?: number;
    isEditing: boolean;
  }>({ nodeIds: [], isEditing: false });

  // Y.Map for storing craft nodes (using same keys as existing system)
  const nodesMap = doc?.getMap("layoutData") as Y.Map<Record<string, unknown>>;
  const metaMap = doc?.getMap("meta") as Y.Map<unknown>;

  // Check collaborators (removed unused variable)

  // Enhanced selection preservation
  const preserveCurrentSelection = useCallback(() => {
    try {
      const editorState = query.getState();
      const selectedNodeIds = Array.from(editorState.events.selected);

      // Get focused element info
      const activeElement = document.activeElement as HTMLElement;
      const focusedNodeId =
        activeElement?.getAttribute?.("data-node-id") || undefined;
      const isContentEditable =
        activeElement?.contentEditable === "true" ||
        activeElement?.tagName === "INPUT" ||
        activeElement?.tagName === "TEXTAREA";
      const cursorPosition =
        isContentEditable && window.getSelection
          ? window.getSelection()?.focusOffset
          : undefined;

      preservedSelection.current = {
        nodeIds: selectedNodeIds,
        focusedNodeId,
        cursorPosition,
        isEditing: isContentEditable && focusedNodeId !== undefined,
      };

      return preservedSelection.current;
    } catch (error) {
      console.error(
        "SelectionPreservingSync: Failed to preserve selection:",
        error
      );
      return { nodeIds: [], isEditing: false };
    }
  }, [query]);

  // Enhanced selection restoration
  const restoreSelection = useCallback(
    (selectionInfo: typeof preservedSelection.current) => {
      try {
        if (!selectionInfo.nodeIds.length && !selectionInfo.focusedNodeId)
          return;

        // Use RAF to ensure DOM is updated
        requestAnimationFrame(() => {
          try {
            // Restore Craft.js node selection
            if (selectionInfo.nodeIds.length > 0) {
              actions.clearEvents();
              selectionInfo.nodeIds.forEach((nodeId) => {
                try {
                  const nodeInfo = query.node(nodeId);
                  if (nodeInfo && nodeInfo.get()) {
                    actions.selectNode(nodeId);
                  }
                } catch {
                  // Node might not exist anymore, skip silently
                }
              });
            }

            // Restore focus and cursor position
            if (selectionInfo.focusedNodeId) {
              const focusedElement = document.querySelector(
                `[data-node-id="${selectionInfo.focusedNodeId}"]`
              ) as HTMLElement;

              if (focusedElement && selectionInfo.isEditing) {
                focusedElement.focus();

                // Restore cursor position for text elements
                if (
                  selectionInfo.cursorPosition !== undefined &&
                  window.getSelection
                ) {
                  // Restore cursor position immediately
                  const selection = window.getSelection();
                  const textNode = focusedElement.firstChild;

                  if (
                    textNode &&
                    textNode.nodeType === Node.TEXT_NODE &&
                    selection
                  ) {
                    const range = document.createRange();
                    const offset = Math.min(
                      selectionInfo.cursorPosition || 0,
                      textNode.textContent?.length || 0
                    );
                    range.setStart(textNode, offset);
                    range.setEnd(textNode, offset);
                    selection.removeAllRanges();
                    selection.addRange(range);
                  }
                }
              }
            }
          } catch (error) {
            console.error(
              "SelectionPreservingSync: Failed to restore selection:",
              error
            );
          }
        });
      } catch (error) {
        console.error(
          "SelectionPreservingSync: Selection restoration failed:",
          error
        );
      }
    },
    [actions, query]
  );

  // Smart diff-based update with safe merging
  const applyRemoteChanges = useCallback(() => {
    if (!nodesMap || !doc || isApplyingRemoteChange.current) {
      return false;
    }

    try {
      isApplyingRemoteChange.current = true;

      // Get current local state first
      const currentLocalState = query.serialize();
      const localNodes = JSON.parse(currentLocalState);

      // Get remote Craft.js state (should be stored directly in layoutData map)
      const remoteCraftState = nodesMap.toJSON() as Record<string, unknown>;

      // Never apply if remote state is empty
      if (!remoteCraftState || Object.keys(remoteCraftState).length === 0) {
        return false;
      }

      const remoteState = JSON.stringify(remoteCraftState);

      // Skip if no actual changes
      if (remoteState === lastRemoteState.current) {
        return false;
      }

      lastRemoteState.current = remoteState;

      // Preserve current selection before applying changes
      const selectionInfo = preserveCurrentSelection();

      // Check if currently editing node was modified
      if (selectionInfo.isEditing && selectionInfo.focusedNodeId) {
        const currentNodeData = remoteCraftState[selectionInfo.focusedNodeId];
        const localNodeData = localNodes[selectionInfo.focusedNodeId];

        // Skip update if currently edited node has local changes
        if (
          currentNodeData &&
          localNodeData &&
          JSON.stringify(currentNodeData) !== JSON.stringify(localNodeData)
        ) {
          return false;
        }
      }

      // Safe merge: only update if remote state is structurally valid
      try {
        // Validate remote state structure
        const hasValidRoot =
          remoteCraftState["ROOT"] &&
          typeof remoteCraftState["ROOT"] === "object";
        if (!hasValidRoot) {
          return false;
        }

        // Apply changes without triggering selection events
        actions.deserialize(remoteState);

        // Restore selection immediately after DOM update
        requestAnimationFrame(() => {
          restoreSelection(selectionInfo);
        });

        // Notify parent
        if (onContentChange) {
          onContentChange(remoteState);
        }

        return true;
      } catch (deserializeError) {
        console.error(
          "SelectionPreservingSync: Failed to deserialize remote state:",
          deserializeError
        );
        // Restore local state if deserialization fails
        try {
          actions.deserialize(currentLocalState);
        } catch (restoreError) {
          console.error(
            "SelectionPreservingSync: Failed to restore local state:",
            restoreError
          );
        }
        return false;
      }
    } catch (error) {
      console.error(
        "SelectionPreservingSync: Failed to apply remote changes:",
        error
      );
      return false;
    } finally {
      // Reset flag after a delay to prevent rapid updates
      setTimeout(() => {
        isApplyingRemoteChange.current = false;
      }, 100);
    }
  }, [
    nodesMap,
    doc,
    preserveCurrentSelection,
    restoreSelection,
    actions,
    query,
    onContentChange,
  ]);

  // Optimized local changes sync with validation and remote protection
  const sendLocalChanges = useCallback(() => {
    if (!nodesMap || !doc || isApplyingRemoteChange.current) {
      return;
    }

    try {
      const currentState = query.serialize();

      // Skip if no changes
      if (currentState === lastLocalState.current) {
        return;
      }

      // Check if remote was recently modified by another user
      const remoteTimestamp = (metaMap?.get("lastModified") as number) || 0;
      const remoteModifiedRecently = remoteTimestamp > Date.now() - 5000; // 5 seconds
      const remoteModifiedBy = metaMap?.get("modifiedBy") as
        | number
        | string
        | undefined;
      const currentClientID = provider?.awareness?.clientID;
      const isRemoteModifiedByOther =
        remoteModifiedBy &&
        currentClientID &&
        remoteModifiedBy !== currentClientID.toString();

      if (remoteModifiedRecently && isRemoteModifiedByOther) {
        console.log(
          "SelectionPreservingSync: Skipping local send - remote was recently modified by another user"
        );
        return;
      }

      // Validate local state before sending
      let currentNodes: Record<string, unknown>;
      try {
        currentNodes = JSON.parse(currentState);
      } catch (parseError) {
        console.error(
          "SelectionPreservingSync: Invalid local state, skipping sync:",
          parseError
        );
        return;
      }

      // Ensure ROOT node exists (basic validation)
      if (!currentNodes["ROOT"]) {
        console.error(
          "SelectionPreservingSync: Missing ROOT node, skipping sync"
        );
        return;
      }

      lastLocalState.current = currentState;

      // Store entire Craft.js state directly to layoutData map (like existing system)
      try {
        doc.transact(() => {
          // Clear existing data and store new state
          nodesMap.clear();
          Object.entries(currentNodes).forEach(([key, value]) => {
            nodesMap.set(key, value as Record<string, unknown>);
          });

          // Update metadata
          if (metaMap) {
            try {
              metaMap.set("lastModified", Date.now());
              metaMap.set(
                "modifiedBy",
                provider?.awareness?.clientID || "unknown"
              );
            } catch (metaError) {
              console.error(
                "SelectionPreservingSync: Failed to update metadata:",
                metaError
              );
            }
          }
        });

        // Successfully stored craft state to server
      } catch (transactionError) {
        console.error(
          "SelectionPreservingSync: Transaction failed:",
          transactionError
        );
        // Reset the lastLocalState to allow retry
        lastLocalState.current = "";
      }
    } catch (error) {
      console.error(
        "SelectionPreservingSync: Failed to send local changes:",
        error
      );
    }
  }, [nodesMap, metaMap, doc, query, provider]);

  // Listen for Y.Map changes with debouncing
  useEffect(() => {
    if (!nodesMap || !isConnected) {
      return;
    }

    const handleYMapChange = (event: Y.YMapEvent<Record<string, unknown>>) => {
      // Skip our own changes
      if (event.transaction.local) {
        return;
      }

      // Apply remote changes immediately without debouncing
      if (!isApplyingRemoteChange.current) {
        applyRemoteChanges();
      }
    };

    nodesMap.observe(handleYMapChange);

    return () => {
      nodesMap.unobserve(handleYMapChange);
    };
  }, [nodesMap, isConnected, applyRemoteChanges]);

  // Listen for Craft.js changes
  useEffect(() => {
    if (!isConnected || !nodesMap) {
      return;
    }

    const handleCraftChange = () => {
      if (isApplyingRemoteChange.current) {
        return;
      }

      // Send local changes immediately without debouncing
      sendLocalChanges();
    };

    const handleTextChange = () => {
      if (isApplyingRemoteChange.current) return;

      // Send local changes immediately without debouncing
      sendLocalChanges();
    };

    const handleManualSync = () => {
      sendLocalChanges();
    };

    // Event listeners
    document.addEventListener("craft-nodes-changed", handleCraftChange);
    document.addEventListener("craft-text-changed", handleTextChange);
    document.addEventListener("craft-manual-sync", handleManualSync);

    return () => {
      document.removeEventListener("craft-nodes-changed", handleCraftChange);
      document.removeEventListener("craft-text-changed", handleTextChange);
      document.removeEventListener("craft-manual-sync", handleManualSync);
    };
  }, [isConnected, nodesMap, sendLocalChanges]);

  // Initial sync with safe content preservation and collaboration awareness
  useEffect(() => {
    if (isConnected && nodesMap) {
      // Check if there are other active collaborators
      const hasActiveCollaborators =
        (provider?.awareness?.getStates()?.size || 0) > 1;

      // Use longer delay if there are other collaborators to avoid overwriting their work
      const syncDelay = hasActiveCollaborators ? 2500 : 1000;

      console.log(
        `SelectionPreservingSync: Initial sync starting in ${syncDelay}ms (collaborators: ${hasActiveCollaborators})`
      );

      // Safe initial sync - prioritize content preservation
      const syncTimeout = setTimeout(() => {
        try {
          const currentState = query.serialize();
          let currentNodes: Record<string, unknown>;

          try {
            currentNodes = JSON.parse(currentState);
          } catch (parseError) {
            console.error(
              "SelectionPreservingSync: Invalid local state during initial sync:",
              parseError
            );
            return;
          }

          const hasLocalContent = Object.keys(currentNodes).length > 1; // More than just ROOT
          const hasValidLocalRoot =
            currentNodes["ROOT"] && typeof currentNodes["ROOT"] === "object";

          // Check if local content is default template
          const localStateStr = JSON.stringify(currentNodes);
          const isDefaultTemplate =
            localStateStr.includes("새로운 웹사이트에 오신 것을 환영합니다") ||
            localStateStr.includes("드래그 앤 드롭 편집기로 아름다운") ||
            (localStateStr.includes("기능 1") &&
              localStateStr.includes("기능 2") &&
              localStateStr.includes("여기에 기능 설명을 추가하세요"));

          // Check remote content
          const remoteCraftState = nodesMap.toJSON() as Record<string, unknown>;
          const remoteHasContent = Boolean(
            remoteCraftState && Object.keys(remoteCraftState).length > 0
          );
          let hasValidRemoteContent = false;
          let isRemoteDefaultTemplate = false;

          if (remoteHasContent) {
            const remoteRoot = remoteCraftState["ROOT"];
            hasValidRemoteContent = Boolean(
              remoteRoot && typeof remoteRoot === "object"
            );

            // Check if remote content is also default template
            const remoteStateStr = JSON.stringify(remoteCraftState);
            isRemoteDefaultTemplate =
              remoteStateStr.includes(
                "새로운 웹사이트에 오신 것을 환영합니다"
              ) ||
              remoteStateStr.includes("드래그 앤 드롭 편집기로 아름다운") ||
              (remoteStateStr.includes("기능 1") &&
                remoteStateStr.includes("기능 2") &&
                remoteStateStr.includes("여기에 기능 설명을 추가하세요"));
          }

          // Check if remote content was recently modified by another user (within last 30 seconds)
          const remoteTimestamp = (metaMap?.get("lastModified") as number) || 0;
          const remoteModifiedRecently = remoteTimestamp > Date.now() - 30000;
          const remoteModifiedBy = metaMap?.get("modifiedBy") as
            | number
            | string
            | undefined;
          const currentClientID = provider?.awareness?.clientID;
          const isRemoteModifiedByOther =
            remoteModifiedBy &&
            currentClientID &&
            remoteModifiedBy !== currentClientID.toString();

          // Smart sync priority logic with recent modification protection
          if (
            hasValidRemoteContent &&
            !isRemoteDefaultTemplate &&
            (!hasLocalContent || !hasValidLocalRoot || isDefaultTemplate)
          ) {
            // Remote has real content, local has no content or default template - apply remote
            console.log(
              "SelectionPreservingSync: Applying remote content (local is empty or default template)"
            );
            applyRemoteChanges();
          } else if (
            hasLocalContent &&
            hasValidLocalRoot &&
            !isDefaultTemplate &&
            (!hasValidRemoteContent || isRemoteDefaultTemplate)
          ) {
            // Protect against overwriting recently modified remote content
            if (
              remoteModifiedRecently &&
              isRemoteModifiedByOther &&
              hasValidRemoteContent
            ) {
              console.log(
                "SelectionPreservingSync: Remote content was recently modified by another user, applying remote instead"
              );
              applyRemoteChanges();
            } else {
              // Local has real content, remote is empty or default template - send local
              console.log(
                "SelectionPreservingSync: Sending local content (remote is empty or default template)"
              );
              sendLocalChanges();
            }
          } else if (
            hasLocalContent &&
            hasValidLocalRoot &&
            !isDefaultTemplate &&
            hasValidRemoteContent &&
            !isRemoteDefaultTemplate
          ) {
            // Both have real content - use timestamp comparison
            const remoteTimestamp =
              (metaMap?.get("lastModified") as number) || 0;
            const localTimestamp = Date.now() - 2000; // Compare against 2 seconds ago (reduced from 5s)

            if (remoteTimestamp > localTimestamp) {
              console.log(
                "SelectionPreservingSync: Applying remote content (newer timestamp)"
              );
              applyRemoteChanges();
            } else {
              console.log(
                "SelectionPreservingSync: Sending local content (local is newer)"
              );
              sendLocalChanges();
            }
          } else if (isDefaultTemplate && isRemoteDefaultTemplate) {
            // Both are default templates - do nothing to avoid unnecessary sync
            console.log(
              "SelectionPreservingSync: Both are default templates, no sync needed"
            );
          } else {
            // Edge case: send local if we have any valid content
            if (hasLocalContent && hasValidLocalRoot && !isDefaultTemplate) {
              console.log(
                "SelectionPreservingSync: Sending local content (edge case)"
              );
              sendLocalChanges();
            } else if (hasValidRemoteContent && !isRemoteDefaultTemplate) {
              console.log(
                "SelectionPreservingSync: Applying remote content (edge case)"
              );
              applyRemoteChanges();
            }
          }
          // If both are empty or invalid, do nothing
        } catch (error) {
          console.error("SelectionPreservingSync: Initial sync failed:", error);
        }
      }, syncDelay); // Dynamic delay based on collaboration context

      return () => {
        clearTimeout(syncTimeout);
      };
    }
  }, [
    isConnected,
    nodesMap,
    applyRemoteChanges,
    sendLocalChanges,
    query,
    metaMap,
    provider,
  ]);

  return null;
};
