package com.qqlei.zhongjiaxin.cache.producer.controller;

import com.alibaba.fastjson.JSONObject;
import com.netflix.hystrix.HystrixCommand;
import com.qqlei.zhongjiaxin.cache.producer.command.GetBrandNameCommand;
import com.qqlei.zhongjiaxin.cache.producer.command.GetCityNameCommand;
import com.qqlei.zhongjiaxin.cache.producer.command.GetProductInfoCommand;
import com.qqlei.zhongjiaxin.cache.producer.model.ProductInfo;
import com.qqlei.zhongjiaxin.cache.producer.model.ShopInfo;
import com.qqlei.zhongjiaxin.cache.producer.prewarm.CachePrewarmThread;
import com.qqlei.zhongjiaxin.cache.producer.rebuild.RebuildCacheQueue;
import com.qqlei.zhongjiaxin.cache.producer.service.CacheService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;

/**
 * 缓存Controller
 * @author Administrator
 *
 */
@Controller
public class CacheController {

	@Resource
	private CacheService cacheService;
	
	@RequestMapping("/testPutCache")
	@ResponseBody
	public String testPutCache(ProductInfo productInfo) {
		cacheService.saveLocalCache(productInfo);
		return "success";
	}
	
	@RequestMapping("/testGetCache")
	@ResponseBody
	public ProductInfo testGetCache(Long id) {
		return cacheService.getLocalCache(id);
	}


	@RequestMapping("/getProductInfo")
	@ResponseBody
	public ProductInfo getProductInfo(Long productId) {
		ProductInfo productInfo = null;

//		productInfo = cacheService.getProductInfoFromReidsCache(productId);
//		System.out.println("=================从redis中获取缓存，商品信息=" + productInfo);
//
//		if(productInfo == null) {
//			productInfo = cacheService.getProductInfoFromLocalCache(productId);
//			System.out.println("=================从ehcache中获取缓存，商品信息=" + productInfo);
//		}

		if(productInfo == null) {
			// 去业务服务拉去数据

//            String url = "http://127.0.0.1:8082/getProductInfo?productId=" + productId;
//            String response = HttpClientUtils.sendGetRequest(url);
//			productInfo = JSONObject.parseObject(response, ProductInfo.class);

			//通过hysrtix做资源隔离
			HystrixCommand<ProductInfo> getProductInfoCommand = new GetProductInfoCommand(productId);
			productInfo = getProductInfoCommand.execute();
			System.out.println("=================从业务服务中获取缓存，商品信息productInfo=" + productInfo);


			//这次请求会从hystrix缓存里取值
//			HystrixCommand<ProductInfo> getProductInfoCommand2 = new GetProductInfoCommand(productId);
//			ProductInfo productInfo2  = getProductInfoCommand2.execute();
//			System.out.println("=================从业务服务中获取缓存，商品信息productInfo2=" + productInfo2);


			GetCityNameCommand getCityNameCommand = new GetCityNameCommand(productInfo.getCityId());
			String cityName = getCityNameCommand.execute();
			productInfo.setCityName(cityName);

			Long brandId = productInfo.getBrandId();
			GetBrandNameCommand getBrandNameCommand = new GetBrandNameCommand(brandId);
			String brandName = getBrandNameCommand.execute();
			productInfo.setBrandName(brandName);
			// 将数据推送到一个内存队列中
//			RebuildCacheQueue rebuildCacheQueue = RebuildCacheQueue.getInstance();
//			rebuildCacheQueue.putProductInfo(productInfo);
		}

		return productInfo;
	}

	@RequestMapping("/getShopInfo")
	@ResponseBody
	public ShopInfo getShopInfo(Long shopId) {
		ShopInfo shopInfo 	;

		shopInfo = cacheService.getShopInfoFromReidsCache(shopId);
		System.out.println("=================从redis中获取缓存，店铺信息=" + shopInfo);

		if(shopInfo == null) {
			shopInfo = cacheService.getShopInfoFromLocalCache(shopId);
			System.out.println("=================从ehcache中获取缓存，店铺信息=" + shopInfo);
		}

		if(shopInfo == null) {
			String shopInfoJSON = "{\"id\":"+shopId+", \"name\": \"手机店铺\", \"level\": 1, \"goodCommentRate\":\"5\"}";
			shopInfo = JSONObject.parseObject(shopInfoJSON, ShopInfo.class);
		}

		return shopInfo;
	}

	@RequestMapping("/prewarmCache")
	@ResponseBody
	public void prewarmCache() {
		new Thread(new CachePrewarmThread()).start();
	}
}
