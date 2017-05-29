package alpha.study.hellojson;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

class User {
	public User() {
		 sex = new StringBuffer();
	}
	
	public User(String name, int age, StringBuffer sex, boolean isChild) {
		this.name = name;
		this.age = age;
		this.sex = sex;
		this.isChild = isChild;
	}

	private String name = "username";
	private int age;
	private StringBuffer sex = null;
	private boolean isChild;

	public String toString() {
		return "{name=" + name + ";age=" + age + ";sex=" + sex + ";isChild=" + isChild + "}";
	}

	public int hashCode() {
		return name.hashCode() * 100 + age;
	}
}

public class GsonTest {
    private static Logger LOGGER = LoggerFactory.getLogger(GsonTest.class);

	public static void main(String[] args) {
		
		//Gson gson = new Gson();

		Gson gson = new GsonBuilder().setPrettyPrinting().create();	
		
		Config config = new Config();
		if (true) {
			String compactFormat = JsonTool.compactFormatJson(config);
			LOGGER.info("compactFormat:\n{}", compactFormat);
			
			String prettyFormat = JsonTool.prettyFormatJson(config);
			LOGGER.info("prettyFormat:\n{}", prettyFormat);
			
			String propertyFormat = JsonTool.propertyFormatJson(config);
			LOGGER.info("propertyFormat:\n{}", propertyFormat);
			
			propertyFormat = JsonTool.propertyFormatJson(null, (JsonElement) null);
			LOGGER.info("null JsonElement propertyFormat:\n{}", propertyFormat);	
			
			config = JsonTool.mergeJson(config, "{active:true,mode:STATIC,server2Config:{rate:0.99999}}");
			LOGGER.info("propertyFormat:\n{}", JsonTool.propertyFormatJson(config));

			return ;
		}
		String json = gson.toJson(config);
		LOGGER.info("默认对象转换成为Json字符串:\n" + json);
		
		config.setactive(true);
		config.setmode(Mode.DYNAMIC);
		config.server1Config.setactive(true);
		config.server1Config.serverIPs[0] = "ip0";
		config.server1Config.serverPorts[0] = 1000;
		json = gson.toJson(config);

		LOGGER.info("修改后对象转换成为Json字符串:\n" + json);
		

		
		
		try {
		
			String filename = "config1.txt";			
			LOGGER.info("从文件" + filename + "转换json：");

			InputStream in = new FileInputStream(filename);
			byte[] b = new byte[1024];
			int length = 0;
			StringBuffer sb = new StringBuffer();
			while ((length = in.read(b)) != -1) {
				sb.append(new String(b, 0, length, "UTF-8"));
			}
			in.close();
			LOGGER.info("配置文件内容:\n" + sb.toString());

			if (true) {
				JsonParser jsonParser = new JsonParser();
				JsonElement jsonElement = jsonParser.parse(sb.toString());
				LOGGER.info("配置文件 JsonParser: {} ", jsonElement.toString());
/*				printJsonString(sb.toString());
				printJsonElement(jsonElement);*/
				
				JsonObject dstJsonObject = jsonParser.parse(gson.toJson(config)).getAsJsonObject();
//				LOGGER.info("修改前，dstJsonObject: {}", gson.toJson(config));
//				LOGGER.info("修改前，config.server2Config.service1Config == null: {}", config.server2Config.service1Config == null);					
//				JsonTool.modifyJsonObject(dstJsonObject, jsonElement.getAsJsonObject());
//				LOGGER.info("修改后，dstJsonObject: {}", gson.toJson(dstJsonObject));
//				config = gson.fromJson(dstJsonObject, Config.class);
//				LOGGER.info("修改后，config: {}", gson.toJson(config));
//				LOGGER.info("修改后，config.server2Config.service1Config == null: {}", config.server2Config.service1Config == null);					
//
//				dstJsonObject = jsonParser.parse(gson.toJson(config)).getAsJsonObject();
				LOGGER.info("修改前，dstJsonObject: {}", gson.toJson(config));
				LOGGER.info("修改前，config.server2Config.service1Config == null: {}", config.server2Config.service1Config == null);					
				JsonElement dstJsonElement = JsonTool.modifyJson(dstJsonObject, jsonElement.getAsJsonObject());
				LOGGER.info("modifyJson 修改后，dstJsonObject: {}", gson.toJson(dstJsonElement));
				config = gson.fromJson(dstJsonObject, Config.class);
				LOGGER.info("modifyJson 修改后，config: {}", gson.toJson(config));
				LOGGER.info("modifyJson 修改后，config.server2Config.service1Config == null: {}", config.server2Config.service1Config == null);					

			}
			
			config = gson.fromJson(sb.toString(), Config.class);
			json = gson.toJson(config);
			LOGGER.info("配置文件转换成Config对象后，再转换成Json字符串:\n" + json);

			if (true) {
				JsonParser jsonParser = new JsonParser();
				JsonElement jsonElement = jsonParser.parse(sb.toString());
				LOGGER.info("JsonParser:\n" + jsonElement.toString());
				// printJsonString(sb.toString());
				LOGGER.info("\n{}", JsonTool.prettyFormatJson(jsonElement));

			}
			LOGGER.info("配置文件内容:\n" + sb.toString());
			User user = gson.fromJson(sb.toString(), User.class);
			json = gson.toJson(user);
			LOGGER.info("配置文件转换成User对象后，再转换成Json字符串:\n" + json);
			
			FileReader fileReader = new FileReader(filename);
			Config config1 = gson.fromJson(fileReader, Config.class);
			LOGGER.info("read file {} to json {} ", filename, gson.toJson(config));		
			
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		if (config.getactive() == true) {
			LOGGER.info("config.active = true");
		}

		switch (config.server1Config.mode) {
		case DEFAULT:
			LOGGER.info("config.server1Config.mode = DEFAULT");
			break;
		case DYNAMIC:
			LOGGER.info("config.server1Config.mode = DYNAMIC");
			break;
		case STATIC:
			LOGGER.info("config.server1Config.mode = STATIC");
			break;
		default:
			break;
		}

		ServiceConfig serviceConfig = new ServiceConfig();
		json = gson.toJson(serviceConfig);
		LOGGER.info("默认对象转换成为Json字符串:\n" + json);

		json = "{\"active\":false,\"mode\":\"DEFAULT\",\"rate\":0.2}";
		serviceConfig = gson.fromJson(json, ServiceConfig.class);
		json = gson.toJson(serviceConfig);
		LOGGER.info("默认对象转换成为Json字符串:\n" + json);
		json = "{\"active\":true,\"mode\":\"DYNAMIC\",\"rate\":0.2}";
		serviceConfig = gson.fromJson(json, ServiceConfig.class);
		json = gson.toJson(serviceConfig);
		LOGGER.info("默认对象转换成为Json字符串:\n" + json);
		json = "{\"active\":true,\"mode\":\"DYNAMIC\",\"rate\":1.0}";
		serviceConfig = gson.fromJson(json, ServiceConfig.class);
		String json1 = gson.toJson(serviceConfig);
		LOGGER.info("默认对象转换成为Json字符串:\n" + json1);
		String json2 = "{\"count\":99}";
		serviceConfig = gson.fromJson(json2, ServiceConfig.class);
		json = gson.toJson(serviceConfig);
		LOGGER.info("默认对象转换成为Json字符串:\n" + json);
		JsonParser jsonParser = new JsonParser();
		JsonObject jobj1 = jsonParser.parse(json1).getAsJsonObject();
		JsonObject jobj2 = jsonParser.parse(json2).getAsJsonObject();
		JsonTool.modifyJsonObject(jobj1, jobj2);
		serviceConfig = gson.fromJson(jobj1, ServiceConfig.class);
		json = gson.toJson(serviceConfig);
		LOGGER.info("修改后: {}", json);
		
		if (true) {
			String tmp = null;

			try {
				tmp = null;
				JsonElement jsonElement = jsonParser.parse(tmp);
				LOGGER.info("JsonParser:{}, {}, {}", tmp, jsonElement.toString(), jsonElement.getClass());
			} catch (Exception e) {
				LOGGER.info("{}, JsonParser Exception:{}", tmp, e.toString());
			}
			try {
				tmp = "";
				JsonElement jsonElement = jsonParser.parse(tmp);
				LOGGER.info("JsonParser:{}, {}, {}", tmp, jsonElement.toString(), jsonElement.getClass());
			} catch (Exception e) {
				LOGGER.info("{}, JsonParser Exception:{}", tmp, e.toString());
			}
			try {
				tmp = "aaaa";
				JsonElement jsonElement = jsonParser.parse(tmp);
				LOGGER.info("JsonParser:{}, {}, {}", tmp, jsonElement.toString(), jsonElement.getClass());
			} catch (Exception e) {
				LOGGER.info("{}, JsonParser Exception:{}", tmp, e.toString());
			}
			try {
				tmp = "{aaaa}";
				JsonElement jsonElement = jsonParser.parse(tmp);
				LOGGER.info("JsonParser:{}, {}, {}", tmp, jsonElement.toString(), jsonElement.getClass());
			} catch (Exception e) {
				LOGGER.info("{}, JsonParser Exception:{}", tmp, e.toString());
			}
			try {
				tmp = "{aaaa:}";
				JsonElement jsonElement = jsonParser.parse(tmp);
				LOGGER.info("JsonParser:{}, {}, {}", tmp, jsonElement.toString(), jsonElement.getClass());
			} catch (Exception e) {
				LOGGER.info("{}, JsonParser Exception:{}", tmp, e.toString());
			}
			try {
				tmp = "{\"aaaa\"}";
				JsonElement jsonElement = jsonParser.parse(tmp);
				LOGGER.info("JsonParser:{}, {}, {}", tmp, jsonElement.toString(), jsonElement.getClass());
			} catch (Exception e) {
				LOGGER.info("{}, JsonParser Exception:{}", tmp, e.toString());
			}
			try {
				tmp = "{\"aaaa\":}";
				JsonElement jsonElement = jsonParser.parse(tmp);
				LOGGER.info("JsonParser:{}, {}, {}", tmp, jsonElement.toString(), jsonElement.getClass());
			} catch (Exception e) {
				LOGGER.info("{}, JsonParser Exception:{}", tmp, e.toString());
			}
			try {
				tmp = "{}";
				JsonElement jsonElement = jsonParser.parse(tmp);
				LOGGER.info("JsonParser:{}, {}, {}", tmp, jsonElement.toString(), jsonElement.getClass());
			} catch (Exception e) {
				LOGGER.info("{}, JsonParser Exception:{}", tmp, e.toString());
			}
			try {
				tmp = "{[]}";
				JsonElement jsonElement = jsonParser.parse(tmp);
				LOGGER.info("JsonParser:{}, {}, {}", tmp, jsonElement.toString(), jsonElement.getClass());
			} catch (Exception e) {
				LOGGER.info("{}, JsonParser Exception:{}", tmp, e.toString());
			}
			try {
				tmp = "[]";
				JsonElement jsonElement = jsonParser.parse(tmp);
				// LOGGER.info("JsonParser:{}, {}, {}", tmp, jsonElement.toString(), jsonElement.getClass());
				LOGGER.info("JsonParser:{}, {}, {}, {}", tmp, jsonElement.toString(), jsonElement.getClass(), jsonElement.getAsJsonArray().size());

			} catch (Exception e) {
				LOGGER.info("{}, JsonParser Exception:{}", tmp, e.toString());
			}
		}
		
	}

}
