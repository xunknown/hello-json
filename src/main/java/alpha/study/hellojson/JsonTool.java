package alpha.study.hellojson;

import java.lang.reflect.Type;
import java.util.Iterator;
import java.util.Map.Entry;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public class JsonTool {
    private static Logger LOGGER = LoggerFactory.getLogger(JsonTool.class);
    
	public static void printJson(Object object, Type type) {
		printJson(new Gson().toJsonTree(object, type));
	}

	public static void printJson(Object object) {
		printJson(new Gson().toJsonTree(object));
	}

	public static void printJson(String json) {
		if (json != null) {
			printJson(new JsonParser().parse(json));
		} else {
			printJson((JsonElement)null);
		}
	}

	public static void printJson(JsonElement jsonElement) {
		printJson(null, jsonElement);
	}
	
	public static void printJson(String property, JsonElement jsonElement) {
		if (jsonElement == null || jsonElement.isJsonPrimitive() || jsonElement.isJsonNull()) {
			if (property != null) {
				LOGGER.info("{}={}", property, jsonElement);
			} else {
				LOGGER.info("{}", jsonElement);
			}
		} else if (jsonElement.isJsonObject()) {
			if (property != null) {
				LOGGER.info("{}:{}", property, jsonElement);
			} else {
				LOGGER.info("{}", jsonElement);				
			}
			JsonObject jsonObject = jsonElement.getAsJsonObject(); // never null?
			Iterator<Entry<String, JsonElement>> jsonObjectIt = jsonObject.entrySet().iterator(); // never null?
			while (jsonObjectIt.hasNext()) {
				Entry<String, JsonElement> jsonEntry = jsonObjectIt.next();
				String propertyFullName = property == null ? jsonEntry.getKey() : property + "." + jsonEntry.getKey();
				printJson(propertyFullName, jsonEntry.getValue());
			}
		} else if (jsonElement.isJsonArray()) {
			if (property != null) {
				LOGGER.info("{}:{}", property, jsonElement);
			} else {
				LOGGER.info("{}", jsonElement);				
			}
			JsonArray jsonArray = jsonElement.getAsJsonArray(); // never null?
			int size = jsonArray.size();
			for (int i = 0; i < size; i++) {
				String propertyFullName = property == null ? "[" + i + "]" : property + "[" + i + "]";
				printJson(propertyFullName, jsonArray.get(i));
			}
		}
	}

	public static String propertyFormat(Object object, Type type) {
		return propertyFormat(new Gson().toJsonTree(object, type));
	}

	public static String propertyFormat(Object object) {
		return propertyFormat(new Gson().toJsonTree(object));
	}

	public static String propertyFormat(String json) {
		if (json != null) {
			return propertyFormat(new JsonParser().parse(json));
		}
		return propertyFormat((JsonElement)null);
	}

	public static String propertyFormat(JsonElement jsonElement) {
		return propertyFormat(null, jsonElement);
	}

	// format an object as property like
	public static String propertyFormat(String property, JsonElement jsonElement) {
		if (jsonElement == null || jsonElement.isJsonPrimitive() || jsonElement.isJsonNull()) {
			if (property != null) {
				return property + "=" + jsonElement + "\n";
			} else {
				return jsonElement + "\n";
			}
		} else if (jsonElement.isJsonObject()) {
			String result = "";
			JsonObject jsonObject = jsonElement.getAsJsonObject(); // never null?
			Iterator<Entry<String, JsonElement>> jsonObjectIt = jsonObject.entrySet().iterator(); // never null?
			while (jsonObjectIt.hasNext()) {
				Entry<String, JsonElement> jsonEntry = jsonObjectIt.next();
				String propertyFullName = property == null ? jsonEntry.getKey() : property + "." + jsonEntry.getKey();
				result += propertyFormat(propertyFullName, jsonEntry.getValue());
			}
			return result;
		} else if (jsonElement.isJsonArray()) {
			String result = "";
			JsonArray jsonArray = jsonElement.getAsJsonArray(); // never null?
			int size = jsonArray.size();
			for (int i = 0; i < size; i++) {
				String propertyFullName = property == null ? "[" + i + "]" : property + "[" + i + "]";
				result += propertyFormat(propertyFullName, jsonArray.get(i));
			}
			return result;
		}
		return null;
	}

    // merge srcObject and srcJson to create a new object, return new object if success, otherwise null
	@SuppressWarnings("unchecked")
	public static <T> T mergeJson(Object srcObject, String srcJson) {
		if (srcObject == null || srcJson == null) {
			return null;
		}
		Gson gson = new Gson();
		JsonElement jsonElement = gson.toJsonTree(srcObject);
		jsonElement = modifyJson(null, jsonElement, new JsonParser().parse(srcJson));
		Object object = gson.fromJson(jsonElement, srcObject.getClass());
		return (T) object;
	}

    // merge srcObject and srcJson to create a new object, return new object if success, otherwise null
	@SuppressWarnings("unchecked")
	public static <T> T mergeJson(Object srcObject, JsonElement srcJson) {
		if (srcObject == null || srcJson == null) {
			return null;
		}
		Gson gson = new Gson();
		JsonElement jsonElement = gson.toJsonTree(srcObject);
		jsonElement = modifyJson(null, jsonElement, srcJson);
		Object object = gson.fromJson(jsonElement, srcObject.getClass());
		return (T) object;
	}

    // modify dstJsonElement with srcJsonElement, return new dstJsonElement if success, otherwise null
    // the caller need assign the return JsonElement to the dstJsonElement to finish modify the dstJsonElement
	public static JsonElement modifyJson(JsonElement dstJsonElement, JsonElement srcJsonElement) {
		return modifyJson(null, dstJsonElement, srcJsonElement);
	}

	public static JsonElement modifyJson(String property, JsonElement dstJsonElement, JsonElement srcJsonElement) {
		if (srcJsonElement == null) {
			// don't modify
			return null;
		}
		if (dstJsonElement == null || dstJsonElement.isJsonNull()) {
			// replace dstJsonElement with srcJsonElement, regardless of mismatch JSON element type
			// but dstJsonElement not modified yet (the caller modify it)
			return srcJsonElement;
		} else if (dstJsonElement.isJsonPrimitive()) {
			if (srcJsonElement.isJsonPrimitive() || srcJsonElement.isJsonNull()) {
				// replace dstJsonElement with srcJsonElement
				// but dstJsonElement not modified yet (the caller modify it)
				return srcJsonElement;
			}
			// don't modify due to mismatch JSON element type
			LOGGER.debug("property: {}, mismatch JSON element type: dstJsonElement is {}, srcJsonElement is {}",
					property, dstJsonElement.getClass(), srcJsonElement.getClass());
			return null;
		} else if (dstJsonElement.isJsonObject()) {
			if (srcJsonElement.isJsonNull()) {
				// replace dstJsonElement with srcJsonElement, regardless of mismatch JSON element type
				return srcJsonElement;
			} else if (srcJsonElement.isJsonObject()) {
				JsonObject dstJsonObject = dstJsonElement.getAsJsonObject(); // never null?
				JsonObject srcJsonObject = srcJsonElement.getAsJsonObject(); // never null?
				Iterator<Entry<String, JsonElement>> srcJsonObjectIt = srcJsonObject.entrySet().iterator(); // never null?
				while (srcJsonObjectIt.hasNext()) {
					Entry<String, JsonElement> srcJsonEntry = srcJsonObjectIt.next();
					String srcEntryProperty = srcJsonEntry.getKey();
					JsonElement srcEntryValue = srcJsonEntry.getValue();
					JsonElement dstEntryValue = dstJsonObject.get(srcEntryProperty);
					String dstOldEntryValue = dstEntryValue == null ? null : dstEntryValue.toString();
					String propertyFullName = property == null ? srcEntryProperty : property + "." + srcEntryProperty;
					dstEntryValue = modifyJson(propertyFullName, dstEntryValue, srcEntryValue);
					if (dstEntryValue != null) {
						// modify dstJsonElement property with srcJsonElement
						dstJsonObject.add(srcEntryProperty, dstEntryValue);
						LOGGER.debug("modified {}:{} with {}", propertyFullName, dstOldEntryValue, dstEntryValue);
					}
				}
				// dstJsonElement has been modified
				return dstJsonElement; // dstJsonObject
			}
			// don't modify due to mismatch JSON element type
			LOGGER.debug("property: {}, mismatch JSON element type: dstJsonElement is {}, srcJsonElement is {}",
					property, dstJsonElement.getClass(), srcJsonElement.getClass());
			return null;
		} else if (dstJsonElement.isJsonArray()) {
			if (srcJsonElement.isJsonNull()) {
				// replace dstJsonElement with srcJsonElement, regardless of mismatch JSON element type
				// but dstJsonElement not modified yet (the caller modify it)
				return srcJsonElement;
			} else if (srcJsonElement.isJsonArray()) {
				JsonArray dstJsonArray = dstJsonElement.getAsJsonArray();
				JsonArray srcJsonArray = srcJsonElement.getAsJsonArray();
				int minSize = Math.min(dstJsonArray.size(), srcJsonArray.size());
				for (int i = 0; i < minSize; i++) {
					JsonElement srcArrayElement = srcJsonArray.get(i);
					JsonElement dstArrayElement = dstJsonArray.get(i);
					String dstOldArrayElement = dstArrayElement == null ? null : dstArrayElement.toString();
					String propertyFullName = property == null ? "[" + i + "]" : property + "[" + i + "]";
					dstArrayElement = modifyJson(propertyFullName, dstArrayElement, srcArrayElement);
					if (dstArrayElement != null) {
						// modify dstJsonArray element with srcJsonArray element
						dstJsonArray.set(i, dstArrayElement);
						LOGGER.debug("modified {}:{} with {}", propertyFullName, dstOldArrayElement, dstArrayElement);
					}
				}

				if (srcJsonArray.size() > dstJsonArray.size()) {
					// add excess elements to dstJsonArray, regardless of mismatch JSON element type
					for (int i = minSize, srcSize = srcJsonArray.size(); i < srcSize; i++) {
						JsonElement srcArrayElement = srcJsonArray.get(i);
						String propertyFullName = property == null ? "[" + i + "]" : property + "[" + i + "]";
						dstJsonArray.add(srcArrayElement);
						LOGGER.debug("modified(appended) {} with {}", propertyFullName, srcArrayElement);
					}
				} else if (srcJsonArray.size() < dstJsonArray.size()) {
					// delete excess elements from dstJsonArray, regardless of mismatch JSON element type
					for (int i = 0; minSize < dstJsonArray.size(); i++) {
						JsonElement dstArrayElement = dstJsonArray.remove(minSize);
						String propertyFullName = property == null ? "[" + (minSize + i) + "]" : property + "[" + (minSize + i) + "]";
						LOGGER.debug("modified(removed) {}:{}", propertyFullName, dstArrayElement);
					}
				}

				// dstJsonElement has been modified
				return dstJsonElement;
			}
			// don't modify due to mismatch JSON element type
			LOGGER.debug("property: {}, mismatch JSON element type: dstJsonElement is {}, srcJsonElement is {}",
					property, dstJsonElement.getClass(), srcJsonElement.getClass());
			return null;
		}
		return null;
	}

	// modify dstJsonObject with srcJsonObject in place
	public static void modifyJsonObject(JsonObject dstJsonObject, JsonObject srcJsonObject) {
		modifyJsonObject(null, dstJsonObject, srcJsonObject);
	}

	public static void modifyJsonObject(String property, JsonObject dstJsonObject, JsonObject srcJsonObject) {
		if (dstJsonObject == null || srcJsonObject == null) return;
		Iterator<Entry<String, JsonElement>> srcJsonObjectIt = srcJsonObject.entrySet().iterator(); // never null?
		while (srcJsonObjectIt.hasNext()) {
			Entry<String, JsonElement> srcJsonEntry = srcJsonObjectIt.next();
			String srcProperty = srcJsonEntry.getKey();
			JsonElement srcJsonElement = srcJsonEntry.getValue(); // never null?
			JsonElement dstJsonElement = dstJsonObject.get(srcProperty);
			String dstOldJsonElement = dstJsonElement == null ? null : dstJsonElement.toString();
			String propertyFullName = property == null ? srcProperty : property + "." + srcProperty;

			if (dstJsonElement == null || dstJsonElement.isJsonNull() || srcJsonElement == null) {
				dstJsonObject.add(srcProperty, srcJsonElement);
				LOGGER.debug("modified {}:{} with {}", propertyFullName, dstOldJsonElement, srcJsonElement);
			} else if (dstJsonElement.isJsonPrimitive()) {
				if (srcJsonElement.isJsonPrimitive() || srcJsonElement.isJsonNull()) {
					dstJsonObject.add(srcProperty, srcJsonElement);
					LOGGER.debug("modified {}:{} with {}", propertyFullName, dstOldJsonElement, srcJsonElement);
				} else {
					LOGGER.debug("property: {}, mismatch JSON element type: dstJsonElement is {}, srcJsonElement is {}",
							propertyFullName, dstJsonElement.getClass(), srcJsonElement.getClass());
				}
			} else if (dstJsonElement.isJsonObject()) {
				if (srcJsonElement.isJsonNull()) {
					dstJsonObject.add(srcProperty, srcJsonElement);
					LOGGER.debug("modified {}:{} with {}", propertyFullName, dstOldJsonElement, srcJsonElement);
				} else if (srcJsonElement.isJsonObject()) {
					modifyJsonObject(propertyFullName, dstJsonElement.getAsJsonObject(), srcJsonElement.getAsJsonObject());
				} else {
					LOGGER.debug("property: {}, mismatch JSON element type: dstJsonElement is {}, srcJsonElement is {}",
							propertyFullName, dstJsonElement.getClass(), srcJsonElement.getClass());
				}
			} else if (dstJsonElement.isJsonArray()) {
				if (srcJsonElement.isJsonNull()) {
					dstJsonObject.add(srcProperty, srcJsonElement);
					LOGGER.debug("modified {}:{} with {}", propertyFullName, dstOldJsonElement, srcJsonElement);
				} else if (srcJsonElement.isJsonArray()) {
					modifyJsonArray(propertyFullName, dstJsonElement.getAsJsonArray(), srcJsonElement.getAsJsonArray());
				} else {
					LOGGER.debug("property: {}, mismatch JSON element type: dstJsonElement is {}, srcJsonElement is {}",
							propertyFullName, dstJsonElement.getClass(), srcJsonElement.getClass());
				}
			}
		}
	}

	// modify dstJsonArray with srcJsonArray in place
	public static void modifyJsonArray(JsonArray dstJsonArray, JsonArray srcJsonArray) {
		modifyJsonArray(null, dstJsonArray, srcJsonArray);
	}
	
	public static void modifyJsonArray(String property, JsonArray dstJsonArray, JsonArray srcJsonArray) {
		if (dstJsonArray == null || srcJsonArray == null) return;
		int minSize = Math.min(dstJsonArray.size(), srcJsonArray.size());
		for (int i = 0; i < minSize; i++) {
			JsonElement srcJsonElement = srcJsonArray.get(i); // never null?
			JsonElement dstJsonElement = dstJsonArray.get(i); // never null?
			String propertyFullName = property == null ? "[" + i +"]" : property + "[" + i +"]";
			if (dstJsonElement == null || dstJsonElement.isJsonNull() || srcJsonElement == null) {
				dstJsonArray.set(i, srcJsonElement);
				LOGGER.debug("modified {}:{} with {}", propertyFullName, dstJsonElement, srcJsonElement);
			} else if (dstJsonElement.isJsonPrimitive()) {
				if (srcJsonElement.isJsonPrimitive() || srcJsonElement.isJsonNull()) {
					dstJsonArray.set(i, srcJsonElement);
					LOGGER.debug("modified {}:{} with {}", propertyFullName, dstJsonElement, srcJsonElement);
				} else {
					LOGGER.debug("property: {}, mismatch JSON element type: dstJsonElement is {}, srcJsonElement is {}",
							propertyFullName, dstJsonElement.getClass(), srcJsonElement.getClass());
					// stop?
				}
			} else if (dstJsonElement.isJsonObject()) {
				if (srcJsonElement.isJsonNull()) {
					dstJsonArray.set(i, srcJsonElement);
					LOGGER.debug("modified(deleted) {}:{} with {}", propertyFullName, dstJsonElement, srcJsonElement);
				} else if (srcJsonElement.isJsonObject()) {
					modifyJsonObject(propertyFullName, dstJsonElement.getAsJsonObject(), srcJsonElement.getAsJsonObject());
				} else {
					LOGGER.debug("property: {}, mismatch JSON element type: dstJsonElement is {}, srcJsonElement is {}",
							propertyFullName, dstJsonElement.getClass(), srcJsonElement.getClass());
					// stop?
				}
			} else if (dstJsonElement.isJsonArray()) {
				if (srcJsonElement.isJsonNull()) {
					dstJsonArray.set(i, srcJsonElement);
					LOGGER.debug("modified {}:{} with {}", propertyFullName, dstJsonElement, srcJsonElement);
				} else if (srcJsonElement.isJsonArray()) {
					modifyJsonArray(propertyFullName, dstJsonElement.getAsJsonArray(), srcJsonElement.getAsJsonArray());
				} else {
					LOGGER.debug("property: {}, mismatch JSON element type: dstJsonElement is {}, srcJsonElement is {}",
							propertyFullName, dstJsonElement.getClass(), srcJsonElement.getClass());
					// stop?
				}
			}
		}

		if (srcJsonArray.size() > dstJsonArray.size()) {
			// add excess elements to dstJsonArray, regardless of mismatch JSON element type
			for (int i = minSize, srcSize = srcJsonArray.size(); i < srcSize; i++) {
				JsonElement srcJsonElement = srcJsonArray.get(i);
				String propertyFullName = property == null ? "[" + i +"]" : property + "[" + i +"]";
				dstJsonArray.add(srcJsonElement);
				LOGGER.debug("modified(appended) {} with {}", propertyFullName, srcJsonElement);
			}
		} else if (srcJsonArray.size() < dstJsonArray.size()) {
			// delete excess elements from dstJsonArray, regardless of mismatch JSON element type
			for (int i = 0; minSize < dstJsonArray.size(); i++) {
				JsonElement dstJsonElement = dstJsonArray.remove(minSize);
				String propertyFullName = property == null ? "[" + (minSize + i) +"]" : property + "[" + (minSize + i) +"]";
				LOGGER.debug("modified(removed) {}:{}", propertyFullName, dstJsonElement);
			}
		}
	}
}
