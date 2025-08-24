package com.backend.easy_to_web.project.infrastructure.external.yjs;

import java.io.ByteArrayInputStream;

public class Decoder {
	private final ByteArrayInputStream buffer;

	public Decoder(byte[] data) {
		this.buffer = new ByteArrayInputStream(data);
	}

	public int readUint8() {
		return buffer.read() & 0xFF; // unsigned byte 값 반환
	}

	public int readVarUint() {
		int result = 0;
		int shift = 0;
		int byteValue;
		do {
			byteValue = readUint8();
			result |= (byteValue & 0x7F) << shift; // 7비트씩 이동하면서 저장
			shift += 7;
		} while ((byteValue & 0x80) != 0); // 최상위 비트가 1이면 계속 읽음
		return result;
	}

	public byte[] readVarUint8Array() {
		int length = readVarUint(); // 배열 길이 읽기
		byte[] data = new byte[length];

		int bytesRead = buffer.read(data, 0, length);
		if (bytesRead < length) {
			throw new RuntimeException("Unexpected end of input stream.");
		}

		return data;
	}
}
