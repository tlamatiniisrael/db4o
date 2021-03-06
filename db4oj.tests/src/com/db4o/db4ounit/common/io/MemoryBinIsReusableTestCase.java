package com.db4o.db4ounit.common.io;

import com.db4o.*;
import com.db4o.config.*;
import com.db4o.internal.*;
import com.db4o.io.*;

import db4ounit.*;

public class MemoryBinIsReusableTestCase implements TestCase {

	private static final String ITEM_NAME = "foo";
	private static final String BIN_URI = "mybin";

	public static class Item {
		public String _name;
		
		public Item(String name) {
			_name = name;
		}
	}
	
	public void test() {
		MemoryStorage origStorage = new MemoryStorage();
		EmbeddedConfiguration origConfig = config(origStorage);
		ObjectContainer origDb = Db4oEmbedded.openFile(origConfig, BIN_URI);
		origDb.store(new Item(ITEM_NAME));
		origDb.close();
		
		MemoryBin origBin = origStorage.bin(BIN_URI);
		byte[] data = origBin.data();
		Assert.areEqual(data.length, origBin.length());
		
		MemoryBin newBin = new MemoryBin(data, new DoublingGrowthStrategy());
		MemoryStorage newStorage = new MemoryStorage();
		newStorage.bin(BIN_URI, newBin);
		ObjectContainer newDb = Db4oEmbedded.openFile(config(newStorage), BIN_URI);
		ObjectSet<Item> result = newDb.query(Item.class);
		Assert.areEqual(1, result.size());
		Assert.areEqual(ITEM_NAME, result.next()._name);
		newDb.close();
	}

	private EmbeddedConfiguration config(MemoryStorage storage) {
		EmbeddedConfiguration config = Db4oEmbedded.newConfiguration();
		config.file().storage(storage);
		config.common().reflectWith(Platform4.reflectorForType(Item.class));
		return config;
	}
	
}
