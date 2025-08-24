package com.backend.easy_to_web.project.infrastructure.external.yjs;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import org.springframework.web.socket.BinaryMessage;

public class Encoder {
	private final ByteArrayOutputStream buffer;

	public Encoder() {
		this.buffer = new ByteArrayOutputStream();
	}

	public void writeUint8(int value) {
		buffer.write(value & 0xFF);
	}

	public void writeBytes(byte[] data) {
		try {
			buffer.write(data);
		} catch (IOException e) {
			throw new RuntimeException("Failed to write bytes", e);
		}
	}

	public void writeVarUint(int value) {
		while (true) {
			int byteValue = value & 0x7F; // 하위 7비트 추출
			value >>>= 7; // 7비트 이동
			if (value == 0) {
				writeUint8(byteValue);
				break;
			} else {
				writeUint8(byteValue | 0x80); // 상위 비트 1 설정 (계속 읽어야 함)
			}
		}
	}

	public void writeVarUint8Array(byte[] data) {
		writeVarUint(data.length); // 배열 길이 먼저 기록
		writeBytes(data); // 바이트 데이터 저장
	}

	public byte[] toUint8Array() {
		return buffer.toByteArray();
	}

	public boolean isEmpty(){
		return buffer.size() == 0;
	}

	public BinaryMessage toBinaryMessage() {
		return new BinaryMessage(toUint8Array());
	}
}

