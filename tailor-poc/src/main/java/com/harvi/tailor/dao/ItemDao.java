package com.harvi.tailor.dao;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import com.harvi.tailor.constants.ItemConstants;
import com.harvi.tailor.constants.ItemConstants.ItemGroup;
import com.harvi.tailor.constants.ItemConstants.ItemType;
import com.harvi.tailor.entities.ApiError;
import com.harvi.tailor.entities.Item;
import com.harvi.tailor.entities.ItemsGroup;
import com.harvi.tailor.entities.Rate;
import com.harvi.tailor.entities.filterbeans.RateFilterBean;
import com.harvi.tailor.utils.Utils;

public class ItemDao {

	private static final Logger LOG = Logger.getLogger(ItemDao.class.getName());

	private static final ItemDao INSTANCE = new ItemDao();

	private static final List<Item> ITEMS = new ArrayList<Item>();

	private static final Map<String, Item> ITEM_ID_TO_ITEM_MAP = new HashMap<>();

	private static final List<ItemsGroup> GROUPED_ITEMS;

	static {
		// All item names:
		// 'Kurta', 'Payjama', 'Pant Payjama', 'Pathani', 'Kurti', 'Jacket', 'Safari',
		// 'Waist Coat', 'Others'
		ITEMS.add(new Item("blazerOrCoat", "Blazer/Coat", ItemGroup.COAT, ItemType.COAT, null));
		ITEMS.add(new Item("suit2p", "2 Piece Suit", ItemGroup.COAT, ItemType.COMBO,
				Arrays.asList("blazerOrCoat", "pant")));
		ITEMS.add(new Item("suit3pV", "3 Piece Suit (V)", ItemGroup.COAT, ItemType.COMBO,
				Arrays.asList("blazerOrCoat", "pant", "waistCoatV")));
		ITEMS.add(new Item("suit3pDB", "3 Piece Suit (DB)", ItemGroup.COAT, ItemType.COMBO,
				Arrays.asList("blazerOrCoat", "pant", "waistCoatV")));
		ITEMS.add(new Item("suit3pDBL", "3 Piece Suit (DB+L)", ItemGroup.COAT, ItemType.COMBO,
				Arrays.asList("blazerOrCoat", "pant", "waistCoatV")));
		ITEMS.add(new Item("jodhpuriSuit", "Jodhpuri Suit", ItemGroup.COAT, ItemType.COMBO,
				Arrays.asList("blazerOrCoat", "pant")));
		ITEMS.add(new Item("tuxedo", "Tuxedo", ItemGroup.COAT, ItemType.COMBO, Arrays.asList("blazerOrCoat", "pant")));
		ITEMS.add(new Item("achkan", "Achkan", ItemGroup.COAT, ItemType.COAT, null));
		ITEMS.add(new Item("hunterCoat", "Hunter Coat", ItemGroup.COAT, ItemType.COAT, null));

		ITEMS.add(new Item("shirt", "Shirt", ItemGroup.SHIRT_PANT, ItemType.SHIRT, null));
		ITEMS.add(new Item("pant", "Pant", ItemGroup.SHIRT_PANT, ItemType.PANT, null));
		ITEMS.add(new Item("chinos", "Chinos", ItemGroup.SHIRT_PANT, ItemType.PANT, null));
		ITEMS.add(new Item("jeans", "Jeans", ItemGroup.SHIRT_PANT, ItemType.PANT, null));
		ITEMS.add(new Item("kurti", "Kurti", ItemGroup.SHIRT_PANT, ItemType.SHIRT, null));
		ITEMS.add(new Item("kurtiDPSF", "Kurti (DP+SF)", ItemGroup.SHIRT_PANT, ItemType.SHIRT, null));

		ITEMS.add(new Item("kurta", "Kurta", ItemGroup.KURTA_PAYJAMA, ItemType.KURTA, null));
		ITEMS.add(new Item("kurtaP", "Kurta (P)", ItemGroup.KURTA_PAYJAMA, ItemType.KURTA, null));
		ITEMS.add(new Item("payjama", "Payjama", ItemGroup.KURTA_PAYJAMA, ItemType.PAYJAMA, null));
		ITEMS.add(new Item("payjamaP", "Payjama (P)", ItemGroup.KURTA_PAYJAMA, ItemType.PAYJAMA, null));
		ITEMS.add(new Item("pantPayjama", "Pant Payjama", ItemGroup.KURTA_PAYJAMA, ItemType.PANT, null));
		ITEMS.add(new Item("pathaniSuit", "Pathani Suit", ItemGroup.KURTA_PAYJAMA, ItemType.COMBO,
				Arrays.asList("kurta", "payjama")));

		ITEMS.add(new Item("jacket", "Jacket", ItemGroup.JACKET, ItemType.JACKET, null));
		ITEMS.add(new Item("waistCoatV", "Waist Coat (V)", ItemGroup.JACKET, ItemType.JACKET, null));
		ITEMS.add(new Item("waistCoatDB", "Waist Coat (DB)", ItemGroup.JACKET, ItemType.JACKET, null));
		ITEMS.add(new Item("waistCoatDBL", "Waist Coat (DB+L)", ItemGroup.JACKET, ItemType.JACKET, null));
		ITEMS.add(new Item("hunterJacket", "Hunter Jacket", ItemGroup.JACKET, ItemType.JACKET, null));

		// safariShirt wont be visible in UI
		ITEMS.add(new Item("safariShirt", "Safari Shirt", ItemGroup.MISCELLANIOUS, ItemType.SAFARI_SHIRT, null));
		ITEMS.add(new Item("safariSuit", "Safari Suit", ItemGroup.MISCELLANIOUS, ItemType.COMBO,
				Arrays.asList("safariShirt", "pant")));
		ITEMS.add(new Item("others", "Others", ItemGroup.MISCELLANIOUS, ItemType.OTHERS, null));

		ITEMS.stream().forEach(item -> ITEM_ID_TO_ITEM_MAP.put(item.getId(), item));

		Map<String, List<Item>> groupNameToItemsMap = ITEMS.stream().collect(Collectors.groupingBy(Item::getGroupName));
		GROUPED_ITEMS = ItemConstants.ItemGroup.GROUP_ORDER.stream()
				.map(groupName -> new ItemsGroup(groupName, groupNameToItemsMap.get(groupName)))
				.collect(Collectors.toList());
	}

	private ItemDao() {

	}

	public static ItemDao getInstance() {
		return INSTANCE;
	}

	public List<Item> getAllItems() {
		return ITEMS;
	}

	public Map<String, Item> getItemIdToItemMap() {
		return ITEM_ID_TO_ITEM_MAP;
	}

	public synchronized List<ItemsGroup> getGroupedItemsWithRates(RateFilterBean rateFilterBean) {
		try {
			Rate rate = RateDao.getInstance().getFilteredRates(rateFilterBean);
			if (rate == null) {
				return GROUPED_ITEMS;
			}
			List<String> itemIdList = Arrays.asList(rate.getItemIds());
			GROUPED_ITEMS.stream().map(itemsGroup -> itemsGroup.getGroupItems()).flatMap(grpItems -> grpItems.stream())
					.forEach(item -> {
						int idx = itemIdList.indexOf(item.getId());
						if (idx != -1) {
							item.setRate(rate.getItemRates()[idx]);
						}
					});

			return GROUPED_ITEMS;
		} catch (Exception e) {
			String shortErrorMsg = "Could not get grouped items";
			ApiError apiError = Utils.createApiError(e, shortErrorMsg, LOG);
			Response response = Response.status(Status.INTERNAL_SERVER_ERROR).entity(apiError).build();
			throw new WebApplicationException(response);
		}
	}
}
