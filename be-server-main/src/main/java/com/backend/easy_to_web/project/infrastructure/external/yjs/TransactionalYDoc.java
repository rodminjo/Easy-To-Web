package com.backend.easy_to_web.project.infrastructure.external.yjs;

import com.yjava.wapper.YDoc;
import com.yjava.wapper.YTransaction;
import java.time.LocalDateTime;
import java.util.concurrent.locks.ReentrantLock;

public class TransactionalYDoc {

  private final YDoc yDoc;
  private LocalDateTime lastEditTime;
  private final ReentrantLock transactionLock = new ReentrantLock();

  public TransactionalYDoc() {
    this.yDoc = new YDoc();
    this.lastEditTime = LocalDateTime.now();
  }

  public boolean isTimeExceeded(long minutes) {
    if (lastEditTime == null) {
      return true;
    }
    return lastEditTime.plusMinutes(minutes).isBefore(LocalDateTime.now());
  }

  public boolean applyUpdate(byte[] diff) {
    transactionLock.lock();
    try {
      YTransaction yTransaction = yDoc.getWriteTransaction();
      byte[] safeDiff = validateOrDefault(diff);
      byte result = yTransaction.applyUpdate(safeDiff);
      yTransaction.commit();

      boolean isSuccess = result == 0;
      if (isSuccess) {
        updateLastEditTime();
      }

      return isSuccess;
    } finally {
      transactionLock.unlock();
    }
  }


  public byte[] encodeStateAsUpdate() {
    return encodeStateAsUpdate(new byte[1]);
  }

  public byte[] encodeStateAsUpdate(byte[] update) {
    transactionLock.lock();
    try {
      YTransaction yTransaction = yDoc.getReadTransaction();
      byte[] safeUpdate = validateOrDefault(update);
      byte[] diff = yTransaction.getStateDiff(safeUpdate);
      yTransaction.commit();
      return diff;
    } finally {
      transactionLock.unlock();
    }
  }

  public byte[] encodeStateVector() {
    transactionLock.lock();
    try {
      YTransaction yTransaction = yDoc.getReadTransaction();
      byte[] vector = yTransaction.getStateVector();
      yTransaction.commit();
      return vector;
    } finally {
      transactionLock.unlock();
    }
  }


  public void destroy(){
    transactionLock.lock();
    try {
      yDoc.destroy();

    } finally {
      transactionLock.unlock();
    }
  }

  private void updateLastEditTime(){
    this.lastEditTime = LocalDateTime.now();
  }

  private byte[] validateOrDefault(byte[] data) {
    return (data == null || data.length == 0) ? new byte[1] : data;
  }
}
