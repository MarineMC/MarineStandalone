package com.marine.io.data;

import java.util.List;

public interface NibbleArray extends List<Byte>, Byteable{
	public void contains(byte nibble);
}