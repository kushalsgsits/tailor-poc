package com.harvi.tailor.constants;

import java.util.Arrays;
import java.util.List;

public class ItemConstants {

	public static class ItemGroup {
		public static final String COAT = "Coat";
		public static final String SHIRT_PANT = "Shirt-Pant";
		public static final String KURTA_PAYJAMA = "Kurta-Payjama";
		public static final String JACKET = "Jacket";
		public static final String MISCELLANIOUS = "Miscellanious";

		public static final List<String> GROUP_ORDER = Arrays.asList(COAT, SHIRT_PANT, KURTA_PAYJAMA, JACKET,
				MISCELLANIOUS);
	}

	public static class ItemType {
		public static final String COAT = "Coat";
		public static final String SHIRT = "Shirt";
		public static final String PANT = "Pant";
		public static final String KURTA = "Kurta";
		public static final String PAYJAMA = "Payjama";
		public static final String JACKET = "Jacket";
		public static final String SAFARI_SHIRT = "Safari Shirt";
		public static final String OTHERS = "Others";
		public static final String COMBO = "Combo";
	}
}
