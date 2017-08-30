package com.aoshi.service.manage;

import com.aoshi.common.asenum.*;
import com.aoshi.controller.base.BaseController;
import com.aoshi.dao.*;
import com.aoshi.domain.*;
import com.aoshi.resolver.ValidatorBreakException;
import com.aoshi.service.base.AsBaseService;
import com.aoshi.service.manage.locality.AsAreaService;
import com.aoshi.service.manage.locality.AsCityService;
import com.aoshi.service.manage.locality.AsProvinceService;
import com.aoshi.util.*;
import net.sf.json.JSONArray;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * 全局礼品操作 Service
 *
 * @author tgb
 * @date 2016年11月2日
 */
@Service
public class PrizeConfigService extends AsBaseService {

    private final static String PAGE_PATH = "manage/prizeconfig/";

    private final static String PAGE_PATH_COUPON = "business/coupon/";

    private final static String PAGE_PATH_SHAKE = "manage/shake/";

    @Autowired
    private AsPrizeConfigMapper asPrizeConfigMapper;

    @Autowired
    private AsPrizeCategoryMapper asPrizeCategoryMapper;

    @Autowired
    private AsYpBaseCategoryMapper asYpBaseCategoryMapper;

    @Autowired
    private AsShopMapper asShopMapper;

    @Autowired
    private AsActivityShakeMapper asActivityShakeMapper;

    @Autowired
    private AsPrizeMapper asPrizeMapper;

    @Autowired
    private AsProvinceService asProvinceService;

    @Autowired
    private AsCityService asCityService;

    @Autowired
    private AsAreaService asAreaService;

    @Autowired
    private AsDictParamMapper asDictParamMapper;

    @Autowired
    private AsPrizeJackpotMapper asPrizeJackpotMapper;

    @Autowired
    private AsShopUserRelateMapper asShopUserRelateMapper;

    @Autowired
    private AsPrizeConfigPushMapper asPrizeConfigPushMapper;

    /**
     * 转盘页面中转控制
     *
     * @param c
     * @return
     * @author tgb 创建时间：2016年11月5日
     * @version 1.0
     */
    public Object page(BaseController c) {

        initialized(c);

        OperaEnum action = OperaEnum.get(pd.getInt("action"));
        switch (action) {

            case ADD:
                break;

            case EDIT:
                int prizeConfigId = pd.getInt("prizeConfigId");
                int isJackpot = pd.getInt("isJackpot");
                Conditions prizeConfig = (Conditions) asPrizeConfigMapper
                        .findByIdWithCoupons(prizeConfigId);
                ValidatorBreakException.NOT_NULL(prizeConfig, 1001, "系统繁忙", 1);
                addViewData("pd", prizeConfig);
                if (isJackpot == 1) {
                    AsPrizeJackpot jackpot = asPrizeJackpotMapper.selectByConfigId(prizeConfigId);
                    addViewData("jackpot", jackpot);
                }
                break;

            case QUERY:
                prizeConfigId = pd.getInt("prizeConfigId");
                isJackpot = pd.getInt("isJackpot");
                prizeConfig = (Conditions) asPrizeConfigMapper
                        .findByIdWithCoupons(prizeConfigId);
                ValidatorBreakException.NOT_NULL(prizeConfig, 1001, "系统繁忙", 1);
                addViewData("pd", prizeConfig);
                if (isJackpot == 1) {
                    AsPrizeJackpot jackpot = asPrizeJackpotMapper.selectByConfigId(prizeConfigId);
                    addViewData("jackpot", jackpot);
                }
                break;

            default:
                ValidatorBreakException.NOT_TRUE(false);
        }

        List<AsYpBaseCategory> prizeTypeList = asYpBaseCategoryMapper
                .queryPrizeListAll(BaseCategoryTypeEnum.REDRAIN_PRIZE_TYPE.getId());
        ArrayList<AsYpBaseCategory> newPrizeTypeList = new ArrayList<AsYpBaseCategory>();
        //判断是否是后台运营管理员
        if (isAdminRole()) {
            List<Conditions> shopAll = asShopMapper.findByShopAll();
            addViewData("shopAll", shopAll);
            for (AsYpBaseCategory aCategory : prizeTypeList) {
                if (aCategory.getName().equals("积分")
                        || aCategory.getName().equals("代金券")
                        || aCategory.getName().equals("礼品券")
                        || aCategory.getName().equals("折扣券")) {
                    newPrizeTypeList.add(aCategory);
                }
            }
        } else {
            String shopId = getUser().getAs_shop_id();
            AsShop asShop = asShopMapper.findByShopId(shopId);
            addViewData("asShop", asShop);
            for (AsYpBaseCategory aCategory : prizeTypeList) {
                if (aCategory.getName().equals("折扣券")
                        || aCategory.getName().equals("代金券")
                        || aCategory.getName().equals("礼品券")) {
                    newPrizeTypeList.add(aCategory);
                }
            }
        }

        addViewData("prizeTypeList", newPrizeTypeList);
        addViewData("action", action.getId());
        addViewData("activityType", ActivityTypeEnum.DRAW_TYPE.getId());
        return renderView(PAGE_PATH + "save");
    }


    /**
     * 转盘礼品操作
     *
     * @param c
     * @return
     * @author tgb
     * @date 2016年11月2日
     */
    public Object opera(BaseController c) {

        initialized(c);

        OperaEnum action = OperaEnum.get(pd.getInt("action"));
        switch (action) {
            case ADD:

                String name = pd.getString("name");
                String categoryName = pd.getString("categoryName");
                Integer counts = pd.getInt("counts");
                Integer shopId = pd.getInt("shopId");
                Integer storeId = pd.getInt("storeId");
                String endTime = pd.getString("endTime");
                String startTime = pd.getString("startTime");
                String inactiveTime = pd.getString("inactiveTime");
                String activeTime = pd.getString("activeTime");
                String createTime = CommonUtils.getCurDate();
                String descp = pd.getString("descp");
                String sn = RandomCodeUtil.getPrizeSn();
                uploadModule(UploadPathEnum.ACTIVITY, "logoUrl");
                String logoUrl = pd.get("logoUrl") == null ? "" : pd.get("logoUrl")
                        .toString();
                ValidatorBreakException.NOT_TRUE(!(logoUrl == null || logoUrl
                        .trim().length() == 0), "请上传礼品图片");

                Integer isJackpot = pd.getIntegerRequired("isJackpot");
                //Integer activityShakeId = pd.getIntegerRequired("activityShakeId");
                Integer categoryId = pd.getIntegerRequired("categoryId");
                String price;

                // 添加礼品配置
                AsPrizeConfig config = new AsPrizeConfig();
                config.setName(name);
                config.setCounts(counts);
                config.setSurplusCounts(counts);
                config.setShopId(shopId);
                config.setStoreId(storeId);
                config.setCreateTime(createTime);
                config.setDescp(descp);
                config.setSn(sn);
                config.setLogoUrl(logoUrl);
                if (!categoryName.equals("礼品券")) {
                    price = pd.getString("price");
                    config.setPrice(new BigDecimal(price));
                }
                config.setActivityType(ActivityTypeEnum.DRAW_TYPE.getId());
                config.setIsJackpot(isJackpot);
                //config.setActivityShakeId(activityShakeId);
                config.setCategoryId(categoryId);
                config.setStatus(0);
                config.setAuditStatus(0);
                long timeDiff;
                if (!categoryName.equals("积分")) {
                    config.setEndTime(endTime);
                    config.setStartTime(startTime);
                    timeDiff = CommonUtils.orderTimeDiff(
                            CommonUtils.getTimeMils(endTime),
                            System.currentTimeMillis());
                    ValidatorBreakException.NOT_TRUE(timeDiff != 0,
                            "结束时间必须大于当前时间");
                }
                config.setActiveTime(activeTime);
                config.setInactiveTime(inactiveTime);
                timeDiff = CommonUtils.orderTimeDiff(
                        CommonUtils.getTimeMils(inactiveTime),
                        System.currentTimeMillis());
                ValidatorBreakException.NOT_TRUE(timeDiff != 0,
                        "失效时间必须大于当前时间");
                operaFlag = asPrizeConfigMapper.insertSelective(config) == 1;
                ValidatorBreakException.NOT_TRUE(operaFlag);
                //如果是大奖礼品就添加到礼品大奖信息表
                if (isJackpot == 1) {
                    AsPrizeJackpot jackpot = new AsPrizeJackpot();
                    jackpot.setPrizeConfigId(config.getPrizeConfigId());
                    jackpot.setActiveTime(activeTime);
                    jackpot.setInactiveTime(inactiveTime);
                    Integer isDefinite = pd.getInt("isDefinite");
                    String intervalTime = pd.getString("intervalTime");
                    jackpot.setIsDefinite(isDefinite);
                    jackpot.setIntervalTime(intervalTime);
                    operaFlag = asPrizeJackpotMapper.insertSelective(jackpot) == 1;
                    ValidatorBreakException.NOT_TRUE(operaFlag);
                }

                break;

            case EDIT:

                int prizeConfigId = pd.getInt("prizeConfigId");
                categoryName = pd.getString("categoryName");
                config = (AsPrizeConfig) asPrizeConfigMapper
                        .selectByPrimaryKey(prizeConfigId);
                ValidatorBreakException.NOT_NULL(config, "系统繁忙");
                Integer status = config.getStatus();
                Integer auditStatus = config.getAuditStatus();
                Integer tempCounts = config.getCounts() - config.getSurplusCounts();
                Integer tempSurplusCounts = pd.getInt("surplusCounts");
                isJackpot = pd.getIntegerRequired("isJackpot");
                //activityShakeId = pd.getIntegerRequired("activityShakeId");
                inactiveTime = pd.getString("inactiveTime");
                activeTime = pd.getString("activeTime");
                endTime = pd.getString("endTime");
                startTime = pd.getString("startTime");
                // 修改礼品配置 status 1 2礼品状态是审核通过的启用 禁用
                if (status == 1 || status == 2) {
                    config.setCounts(tempCounts + tempSurplusCounts);
                    config.setSurplusCounts(tempSurplusCounts);
                    config.setIsJackpot(isJackpot);
                    if (!categoryName.equals("积分")) {
                        config.setEndTime(endTime);
                        config.setStartTime(startTime);
                        timeDiff = CommonUtils.orderTimeDiff(
                                CommonUtils.getTimeMils(endTime),
                                System.currentTimeMillis());
                        ValidatorBreakException.NOT_TRUE(timeDiff != 0,
                                "结束时间必须大于当前时间");
                    } else {
                        config.setEndTime("");
                        config.setStartTime("");
                    }
                    config.setInactiveTime(inactiveTime);
                    config.setActiveTime(activeTime);
                    timeDiff = CommonUtils.orderTimeDiff(
                            CommonUtils.getTimeMils(inactiveTime),
                            System.currentTimeMillis());
                    ValidatorBreakException.NOT_TRUE(timeDiff != 0,
                            "失效时间必须大于当前时间");
                    //config.setActivityShakeId(activityShakeId);
                    operaFlag = asPrizeConfigMapper.updateByPrimaryKey(config) == 1;
                    ValidatorBreakException.NOT_TRUE(operaFlag);

                    //更新物品表的时间
                    Map<String, Object> map = new HashMap<>();
                    map.put("prizeConfigId", prizeConfigId);
                    map.put("inactiveTime", inactiveTime);
                    map.put("activeTime", activeTime);
                    map.put("endTime", endTime);
                    map.put("startTime", startTime);
                    asPrizeMapper.updateTimeByConfigId(map);

                    //如果是大奖礼品就更新礼品大奖信息表
                    if (isJackpot == 1) {
                        AsPrizeJackpot jackpot = asPrizeJackpotMapper.selectByConfigId(prizeConfigId);
                        ValidatorBreakException.NOT_NULL(jackpot, "系统繁忙");
                        jackpot.setActiveTime(activeTime);
                        jackpot.setInactiveTime(inactiveTime);
                        Integer isDefinite = pd.getInt("isDefinite");
                        String intervalTime = pd.getString("intervalTime");
                        jackpot.setIsDefinite(isDefinite);
                        jackpot.setIntervalTime(intervalTime);
                        operaFlag = asPrizeJackpotMapper.updateByPrimaryKey(jackpot) == 1;
                        ValidatorBreakException.NOT_TRUE(operaFlag);
                    }

                    //auditStatus 0 2 礼品状态是未审核通过的未审核 驳回
                } else if (auditStatus == 0 || auditStatus == 2) {

                    name = pd.getString("name");
                    counts = pd.getInt("counts");
                    shopId = pd.getInt("shopId");
                    storeId = pd.getInt("storeId");
                    descp = pd.getString("descp");
                    uploadModule(UploadPathEnum.ACTIVITY, "logoUrl");
                    logoUrl = pd.get("logoUrl") == null ? config.getLogoUrl() : pd.get("logoUrl")
                            .toString();
                    categoryId = pd.getIntegerRequired("categoryId");

                    if (!categoryName.equals("礼品券")) {
                        price = pd.getString("price");
                        config.setPrice(new BigDecimal(price));
                    } else {
                        config.setPrice(new BigDecimal("0"));
                    }
                    config.setName(name);
                    config.setCounts(counts);
                    config.setSurplusCounts(counts);
                    config.setShopId(shopId);
                    config.setStoreId(storeId);
                    config.setDescp(descp);
                    config.setLogoUrl(logoUrl);
                    config.setActivityType(ActivityTypeEnum.DRAW_TYPE.getId());
                    config.setIsJackpot(isJackpot);
                    //config.setActivityShakeId(activityShakeId);
                    config.setCategoryId(categoryId);

                    if (!categoryName.equals("积分")) {
                        endTime = pd.getString("endTime");
                        startTime = pd.getString("startTime");
                        config.setEndTime(endTime);
                        config.setStartTime(startTime);
                        timeDiff = CommonUtils.orderTimeDiff(
                                CommonUtils.getTimeMils(endTime),
                                System.currentTimeMillis());
                        ValidatorBreakException.NOT_TRUE(timeDiff != 0,
                                "结束时间必须大于当前时间");
                    } else {
                        config.setEndTime("");
                        config.setStartTime("");
                    }
                    config.setInactiveTime(inactiveTime);
                    config.setActiveTime(activeTime);
                    timeDiff = CommonUtils.orderTimeDiff(
                            CommonUtils.getTimeMils(inactiveTime),
                            System.currentTimeMillis());
                    ValidatorBreakException.NOT_TRUE(timeDiff != 0,
                            "失效时间必须大于当前时间");

                    operaFlag = asPrizeConfigMapper
                            .updateByPrimaryKeyWithBLOBs(config) == 1;
                    ValidatorBreakException.NOT_TRUE(operaFlag);
                    //如果是大奖礼品就更新礼品大奖信息表
                    if (isJackpot == 1) {
                        AsPrizeJackpot jackpot = asPrizeJackpotMapper.selectByConfigId(prizeConfigId);
                        ValidatorBreakException.NOT_NULL(jackpot, "系统繁忙");
                        jackpot.setActiveTime(activeTime);
                        jackpot.setInactiveTime(inactiveTime);
                        Integer isDefinite = pd.getInt("isDefinite");
                        String intervalTime = pd.getString("intervalTime");
                        jackpot.setIsDefinite(isDefinite);
                        jackpot.setIntervalTime(intervalTime);
                        operaFlag = asPrizeJackpotMapper.updateByPrimaryKey(jackpot) == 1;
                        ValidatorBreakException.NOT_TRUE(operaFlag);
                    }

                }

                break;
            default:
                ValidatorBreakException.NOT_TRUE(false);
        }

        return renderSuccess();
    }

    /**
     * 摇奖页面跳转控制
     *
     * @param c
     * @return
     * @author tgb 创建时间：2017年2月23日
     * @version 1.0
     */
    public Object shakePage(BaseController c) {

        initialized(c);

        OperaEnum action = OperaEnum.get(pd.getInt("action"));
        switch (action) {

            case ADD:
                break;

            case EDIT:
                int prizeConfigId = pd.getInt("prizeConfigId");
                int isJackpot = pd.getInt("isJackpot");
                Conditions prizeConfig = (Conditions) asPrizeConfigMapper
                        .findByIdWithCoupons(prizeConfigId);
                ValidatorBreakException.NOT_NULL(prizeConfig, 1001, "系统繁忙", 1);
                addViewData("pd", prizeConfig);
                if (isJackpot == 1) {
                    AsPrizeJackpot jackpot = asPrizeJackpotMapper.selectByConfigId(prizeConfigId);
                    addViewData("jackpot", jackpot);
                }
                break;

            case QUERY:
                prizeConfigId = pd.getInt("prizeConfigId");
                isJackpot = pd.getInt("isJackpot");
                prizeConfig = (Conditions) asPrizeConfigMapper
                        .findByIdWithCoupons(prizeConfigId);
                ValidatorBreakException.NOT_NULL(prizeConfig, 1001, "系统繁忙", 1);
                if (isJackpot == 1) {
                    AsPrizeJackpot jackpot = asPrizeJackpotMapper.selectByConfigId(prizeConfigId);
                    addViewData("jackpot", jackpot);
                }
                addViewData("pd", prizeConfig);
                break;

            default:
                ValidatorBreakException.NOT_TRUE(false);
        }
        if (isAdminRole()) {
            //查找澳视商户ascmMallManager
            AsShop asShop = asShopMapper.findByShopName();
            addViewData("asShop", asShop);
        } else {
            String shopId = getUser().getAs_shop_id();
            AsShop asShop = asShopMapper.findByShopId(shopId);
            addViewData("asShop", asShop);
        }

        List<AsYpBaseCategory> prizeTypeList = asYpBaseCategoryMapper
                .queryPrizeListAll(BaseCategoryTypeEnum.REDRAIN_PRIZE_TYPE
                        .getId());
        ArrayList<AsYpBaseCategory> newPrizeTypeList = new ArrayList<AsYpBaseCategory>();

        for (AsYpBaseCategory aCategory : prizeTypeList) {
            if (aCategory.getName().equals("积分")
                    || aCategory.getName().equals("实物礼品")
                    || aCategory.getName().equals("现金红包")) {
                if (aCategory.getName().equals("实物礼品")) {
                    newPrizeTypeList.add(0, aCategory);
                } else {
                    newPrizeTypeList.add(aCategory);
                }
            }
        }

        addViewData("activityType", ActivityTypeEnum.SHAKE_TYPE.getId());
        addViewData("prizeTypeList", newPrizeTypeList);
        addViewData("action", action.getId());
        return renderView(PAGE_PATH_SHAKE + "save");
    }

    /**
     * 摇奖礼品操作
     *
     * @param c
     * @return
     * @author tgb
     * @date 2016年11月2日
     */
    public Object shakeOpera(BaseController c) {

        initialized(c);

        OperaEnum action = OperaEnum.get(pd.getInt("action"));
        switch (action) {
            case ADD:

                String name = pd.getString("name");
                String categoryName = pd.getString("categoryName");
                Integer counts = pd.getInt("counts");
                Integer shopId = pd.getInt("shopId");
                Integer storeId = pd.getInt("storeId");
                String endTime = pd.getString("endTime");
                String startTime = pd.getString("startTime");
                String inactiveTime = pd.getString("inactiveTime");
                String activeTime = pd.getString("activeTime");
                String createTime = CommonUtils.getCurDate();
                String descp = pd.getString("descp");
                String sn = RandomCodeUtil.getPrizeSn();
                uploadModule(UploadPathEnum.ACTIVITY, "logoUrl");
                String logoUrl = pd.get("logoUrl") == null ? "" : pd.get("logoUrl")
                        .toString();
                ValidatorBreakException.NOT_TRUE(!(logoUrl == null || logoUrl
                        .trim().length() == 0), "请上传礼品图片");

                Integer isJackpot = pd.getIntegerRequired("isJackpot");
                //Integer activityShakeId = pd.getIntegerRequired("activityShakeId");
                Integer categoryId = pd.getIntegerRequired("categoryId");
                String price;

                // 添加礼品配置
                AsPrizeConfig config = new AsPrizeConfig();
                config.setName(name);
                config.setCounts(counts);
                config.setSurplusCounts(counts);
                config.setShopId(shopId);
                config.setStoreId(storeId);
                config.setCreateTime(createTime);
                config.setDescp(descp);
                config.setSn(sn);
                config.setLogoUrl(logoUrl);
                if (!categoryName.equals("实物礼品")) {
                    price = pd.getString("price");
                    config.setPrice(new BigDecimal(price));
                }
                config.setActivityType(ActivityTypeEnum.SHAKE_TYPE.getId());
                config.setIsJackpot(isJackpot);
                // config.setActivityShakeId(activityShakeId);
                config.setCategoryId(categoryId);
                config.setStatus(0);
                config.setAuditStatus(0);
                long timeDiff;
                if (categoryName.equals("实物礼品")) {
                    config.setEndTime(endTime);
                    config.setStartTime(startTime);
                    timeDiff = CommonUtils.orderTimeDiff(
                            CommonUtils.getTimeMils(endTime),
                            System.currentTimeMillis());
                    ValidatorBreakException.NOT_TRUE(timeDiff != 0,
                            "结束时间必须大于当前时间");
                }
                config.setActiveTime(activeTime);
                config.setInactiveTime(inactiveTime);
                timeDiff = CommonUtils.orderTimeDiff(
                        CommonUtils.getTimeMils(inactiveTime),
                        System.currentTimeMillis());
                ValidatorBreakException.NOT_TRUE(timeDiff != 0,
                        "失效时间必须大于当前时间");
                operaFlag = asPrizeConfigMapper.insertSelective(config) == 1;
                ValidatorBreakException.NOT_TRUE(operaFlag);
                //如果是大奖礼品就添加到礼品大奖信息表
                if (isJackpot == 1) {
                    AsPrizeJackpot jackpot = new AsPrizeJackpot();
                    jackpot.setPrizeConfigId(config.getPrizeConfigId());
                    jackpot.setActiveTime(activeTime);
                    jackpot.setInactiveTime(inactiveTime);
                    Integer isDefinite = pd.getInt("isDefinite");
                    String intervalTime = pd.getString("intervalTime");
                    jackpot.setIsDefinite(isDefinite);
                    jackpot.setIntervalTime(intervalTime);
                    operaFlag = asPrizeJackpotMapper.insertSelective(jackpot) == 1;
                    ValidatorBreakException.NOT_TRUE(operaFlag);
                }

                break;

            case EDIT:

                int prizeConfigId = pd.getInt("prizeConfigId");
                categoryName = pd.getString("categoryName");
                config = (AsPrizeConfig) asPrizeConfigMapper
                        .selectByPrimaryKey(prizeConfigId);
                ValidatorBreakException.NOT_NULL(config, "系统繁忙");
                Integer status = config.getStatus();
                Integer auditStatus = config.getAuditStatus();
                Integer tempCounts = config.getCounts() - config.getSurplusCounts();
                Integer tempSurplusCounts = pd.getInt("surplusCounts");
                isJackpot = pd.getIntegerRequired("isJackpot");
                //activityShakeId = pd.getIntegerRequired("activityShakeId");
                inactiveTime = pd.getString("inactiveTime");
                activeTime = pd.getString("activeTime");
                endTime = pd.getString("endTime");
                startTime = pd.getString("startTime");
                // 修改礼品配置
                if (status == 1 || status == 2) {
                    config.setCounts(tempCounts + tempSurplusCounts);
                    config.setSurplusCounts(tempSurplusCounts);
                    config.setIsJackpot(isJackpot);
                    //config.setActivityShakeId(activityShakeId);
                    if (categoryName.equals("实物礼品")) {
                        config.setEndTime(endTime);
                        config.setStartTime(startTime);
                        timeDiff = CommonUtils.orderTimeDiff(
                                CommonUtils.getTimeMils(endTime),
                                System.currentTimeMillis());
                        ValidatorBreakException.NOT_TRUE(timeDiff != 0,
                                "结束时间必须大于当前时间");
                    } else {
                        config.setEndTime("");
                        config.setStartTime("");
                    }
                    config.setInactiveTime(inactiveTime);
                    config.setActiveTime(activeTime);
                    timeDiff = CommonUtils.orderTimeDiff(
                            CommonUtils.getTimeMils(inactiveTime),
                            System.currentTimeMillis());
                    ValidatorBreakException.NOT_TRUE(timeDiff != 0,
                            "失效时间必须大于当前时间");
                    operaFlag = asPrizeConfigMapper.updateByPrimaryKey(config) == 1;

                    //更新物品表的时间
                    Map<String, Object> map = new HashMap<>();
                    map.put("prizeConfigId", prizeConfigId);
                    map.put("inactiveTime", inactiveTime);
                    map.put("activeTime", activeTime);
                    map.put("endTime", endTime);
                    map.put("startTime", startTime);
                    asPrizeMapper.updateTimeByConfigId(map);

                    //如果是大奖礼品就更新礼品大奖信息表
                    if (isJackpot == 1) {
                        AsPrizeJackpot jackpot = asPrizeJackpotMapper.selectByConfigId(prizeConfigId);
                        ValidatorBreakException.NOT_NULL(jackpot, "系统繁忙");
                        jackpot.setActiveTime(activeTime);
                        jackpot.setInactiveTime(inactiveTime);
                        Integer isDefinite = pd.getInt("isDefinite");
                        String intervalTime = pd.getString("intervalTime");
                        jackpot.setIsDefinite(isDefinite);
                        jackpot.setIntervalTime(intervalTime);
                        operaFlag = asPrizeJackpotMapper.updateByPrimaryKey(jackpot) == 1;
                        ValidatorBreakException.NOT_TRUE(operaFlag);
                    }
                    ValidatorBreakException.NOT_TRUE(operaFlag);
                } else if (auditStatus == 0 || auditStatus == 2) {

                    name = pd.getString("name");
                    counts = pd.getInt("counts");
                    shopId = pd.getInt("shopId");
                    storeId = pd.getInt("storeId");
                    descp = pd.getString("descp");
                    uploadModule(UploadPathEnum.ACTIVITY, "logoUrl");
                    logoUrl = pd.get("logoUrl") == null ? config.getLogoUrl() : pd
                            .get("logoUrl").toString();
                    categoryId = pd.getIntegerRequired("categoryId");

                    if (!categoryName.equals("实物礼品")) {
                        price = pd.getString("price");
                        config.setPrice(new BigDecimal(price));
                    } else {
                        config.setPrice(new BigDecimal("0"));
                    }
                    config.setName(name);
                    config.setCounts(counts);
                    config.setSurplusCounts(counts);
                    config.setShopId(shopId);
                    config.setStoreId(storeId);
                    config.setDescp(descp);
                    config.setLogoUrl(logoUrl);
                    config.setActivityType(ActivityTypeEnum.SHAKE_TYPE.getId());
                    config.setIsJackpot(isJackpot);
                    //config.setActivityShakeId(activityShakeId);
                    config.setCategoryId(categoryId);
                    if (categoryName.equals("实物礼品")) {
                        config.setEndTime(endTime);
                        config.setStartTime(startTime);
                        timeDiff = CommonUtils.orderTimeDiff(
                                CommonUtils.getTimeMils(endTime),
                                System.currentTimeMillis());
                        ValidatorBreakException.NOT_TRUE(timeDiff != 0,
                                "结束时间必须大于当前时间");
                    } else {
                        config.setEndTime("");
                        config.setStartTime("");
                    }
                    config.setInactiveTime(inactiveTime);
                    config.setActiveTime(activeTime);
                    timeDiff = CommonUtils.orderTimeDiff(
                            CommonUtils.getTimeMils(inactiveTime),
                            System.currentTimeMillis());
                    ValidatorBreakException.NOT_TRUE(timeDiff != 0,
                            "失效时间必须大于当前时间");

                    operaFlag = asPrizeConfigMapper
                            .updateByPrimaryKeyWithBLOBs(config) == 1;
                    ValidatorBreakException.NOT_TRUE(operaFlag);

                    //如果是大奖礼品就更新礼品大奖信息表
                    if (isJackpot == 1) {
                        AsPrizeJackpot jackpot = asPrizeJackpotMapper.selectByConfigId(prizeConfigId);
                        ValidatorBreakException.NOT_NULL(jackpot, "系统繁忙");
                        jackpot.setActiveTime(activeTime);
                        jackpot.setInactiveTime(inactiveTime);
                        Integer isDefinite = pd.getInt("isDefinite");
                        String intervalTime = pd.getString("intervalTime");
                        jackpot.setIsDefinite(isDefinite);
                        jackpot.setIntervalTime(intervalTime);
                        operaFlag = asPrizeJackpotMapper.updateByPrimaryKey(jackpot) == 1;
                        ValidatorBreakException.NOT_TRUE(operaFlag);
                    }

                }

                break;
            default:
                ValidatorBreakException.NOT_TRUE(false);
        }

        return renderSuccess();
    }

    /**
     * 优惠券页面中转控制
     *
     * @param c
     * @return
     * @author tgb 创建时间：2016年10月27日
     * @version 1.0
     */
    public Object couponsPage(BaseController c) {

        initialized(c);

        OperaEnum action = OperaEnum.get(pd.getInt("action"));
        switch (action) {

            case ADD:
                break;

            case EDIT:

                int prizeConfigId = pd.getInt("prizeConfigId");
                Conditions prizeConfig = (Conditions) asPrizeConfigMapper
                        .findByIdWithCoupons(prizeConfigId);
                ValidatorBreakException.NOT_NULL(prizeConfig, 1001, "系统繁忙", 1);
            /*prizeConfig.putData("endTime", CommonUtils.getDateStr(prizeConfig
					.get("endTime").toString(), CommonStr.dateTimePattern));
			prizeConfig.putData("startTime", CommonUtils.getDateStr(prizeConfig
					.get("startTime").toString(), CommonStr.dateTimePattern));*/
                addViewData("pd", prizeConfig);
                break;

            case QUERY:
                prizeConfigId = pd.getInt("prizeConfigId");
                prizeConfig = (Conditions) asPrizeConfigMapper
                        .findByIdWithCoupons(prizeConfigId);
                ValidatorBreakException.NOT_NULL(prizeConfig, 1001, "系统繁忙", 1);
			/*prizeConfig.putData("endTime", CommonUtils.getDateStr(prizeConfig
					.get("endTime").toString(), CommonStr.dateTimePattern));
			prizeConfig.putData("startTime", CommonUtils.getDateStr(prizeConfig
					.get("startTime").toString(), CommonStr.dateTimePattern));*/
                addViewData("pd", prizeConfig);
                break;

            default:
                ValidatorBreakException.NOT_TRUE(false);
        }
        pd.put("type", BaseCategoryTypeEnum.REDRAIN_COUPON_TYPE.getId());
        List<Object> tagTypeList = queryModule(asYpBaseCategoryMapper,
                "queryCouponsListAll", false);
        addViewData("tagTypeList", tagTypeList);

        if (isAdminRole()) {
            List<Conditions> shopAll = asShopMapper.findByShopAll();
            addViewData("shopAll", shopAll);
        } else {
            String shopId = getUser().getAs_shop_id();
            AsShop asShop = asShopMapper.findByShopId(shopId);
            addViewData("asShop", asShop);
        }

        List<AsYpBaseCategory> prizeTypeList = asYpBaseCategoryMapper
                .queryPrizeListAll(BaseCategoryTypeEnum.REDRAIN_PRIZE_TYPE.getId());
        ArrayList<AsYpBaseCategory> newPrizeTypeList = new ArrayList<AsYpBaseCategory>();
        for (AsYpBaseCategory aCategory : prizeTypeList) {
            if (aCategory.getName().equals("折扣券")
                    || aCategory.getName().equals("代金券")
                    || aCategory.getName().equals("礼品券")) {
                newPrizeTypeList.add(aCategory);
            }
        }

        addViewData("prizeTypeList", newPrizeTypeList);
        addViewData("action", action.getId());
        return renderView(PAGE_PATH_COUPON + "save");
    }

    /**
     * 全局优惠券操作
     *
     * @param c
     * @return
     * @author tgb 创建时间：2016年10月27日
     * @version 1.0
     */
    public Object couponsOpera(BaseController c) {

        initialized(c);

        OperaEnum action = OperaEnum.get(pd.getInt("action"));
        switch (action) {
            case ADD:

                String name = pd.getString("name");
                String categoryName = pd.getString("categoryName");
                Integer counts = pd.getInt("counts");
                Integer shopId = pd.getInt("shopId");
                Integer storeId = pd.getInt("storeId");
                String endTime = pd.getString("endTime");
                String startTime = pd.getString("startTime");
                String inactiveTime = pd.getString("inactiveTime");
                String activeTime = pd.getString("activeTime");
                String createTime = CommonUtils.getCurDate();
                String descp = pd.getString("descp");
                String sn = RandomCodeUtil.getPrizeSn();
                //uploadModule1(UploadPathEnum.ACTIVITY, 1);
                // uploadModule1(UploadPathEnum.ACTIVITY, 2);
			/*String logoUrl =  StringUtils.isNotBlank(pd.getString("img1")) ? pd.getString("img1")
					.toString() : "";
			ValidatorBreakException.NOT_TRUE(!(logoUrl == null || logoUrl
					.trim().length() == 0), "请上传优惠券logo图片");*/
			/*
			 * String backUrl = StringUtils.isNotBlank(pd.getString("img2")) ?
			 * pd.getString("img2") .toString() : "";
			 * ValidatorBreakException.NOT_TRUE(!(backUrl == null || backUrl
			 * .trim().length() == 0), "请上传优惠券背景图片");
			 */
                String price = pd.getString("price");

                Integer categoryId = pd.getIntegerRequired("categoryId");
                Integer couponTagId = pd.getIntegerRequired("couponTagId");

                // 添加优惠券配置
                AsPrizeConfig config = new AsPrizeConfig();
                config.setName(name);
                config.setCounts(counts);
                config.setSurplusCounts(counts);
                config.setShopId(shopId);
                config.setStoreId(storeId);
                if (!categoryName.equals("礼品券")) {
                    price = pd.getString("price");
                    config.setPrice(new BigDecimal(price));
                }
                config.setEndTime(endTime);
                config.setStartTime(startTime);
                config.setActiveTime(activeTime);
                config.setInactiveTime(inactiveTime);
                config.setCreateTime(createTime);
                config.setDescp(descp);
                config.setSn(sn);
                //config.setLogoUrl(logoUrl);
                //config.setBackUrl(backUrl);
                config.setActivityType(ActivityTypeEnum.COUPON_TYPE.getId());
                config.setCategoryId(categoryId);
                config.setAuditStatus(0);
                config.setStatus(0);
                long timeDiff = CommonUtils.orderTimeDiff(
                        CommonUtils.getTimeMils(endTime),
                        System.currentTimeMillis());
                ValidatorBreakException.NOT_TRUE(timeDiff != 0, "结束时间必须大于当前时间");
                timeDiff = CommonUtils.orderTimeDiff(
                        CommonUtils.getTimeMils(inactiveTime),
                        System.currentTimeMillis());
                ValidatorBreakException.NOT_TRUE(timeDiff != 0,
                        "失效时间必须大于当前时间");
                operaFlag = asPrizeConfigMapper.insertSelective(config) == 1;
                ValidatorBreakException.NOT_TRUE(operaFlag);
                // 更新优惠劵标签关系表
                AsPrizeCategory asPrizeCategory = new AsPrizeCategory();
                asPrizeCategory.setPrizeConfigId(config.getPrizeConfigId());
                asPrizeCategory.setCategoryId(couponTagId);
                asPrizeCategory.setCreateTime(createTime);
                operaFlag = asPrizeCategoryMapper.insertSelective(asPrizeCategory) == 1;
                ValidatorBreakException.NOT_TRUE(operaFlag);
                break;

            case EDIT:

                int prizeConfigId = pd.getInt("prizeConfigId");
                config = (AsPrizeConfig) asPrizeConfigMapper
                        .selectByPrimaryKey(prizeConfigId);
                ValidatorBreakException.NOT_NULL(config, "系统繁忙");
                Integer status = config.getStatus();
                Integer auditStatus = config.getAuditStatus();
                Integer tempCounts = config.getCounts() - config.getSurplusCounts();
                Integer tempSurplusCounts = pd.getInt("surplusCounts");
                endTime = pd.getString("endTime");
                startTime = pd.getString("startTime");
                inactiveTime = pd.getString("inactiveTime");
                activeTime = pd.getString("activeTime");

                // 修改优惠券配置
                if (status == 1 || status == 2) {

                    timeDiff = CommonUtils.orderTimeDiff(
                            CommonUtils.getTimeMils(endTime),
                            System.currentTimeMillis());
                    ValidatorBreakException.NOT_TRUE(timeDiff != 0,
                            "结束时间必须大于当前时间");
                    timeDiff = CommonUtils.orderTimeDiff(
                            CommonUtils.getTimeMils(inactiveTime),
                            System.currentTimeMillis());
                    ValidatorBreakException.NOT_TRUE(timeDiff != 0,
                            "失效时间必须大于当前时间");
                    config.setCounts(tempCounts + tempSurplusCounts);
                    config.setSurplusCounts(tempSurplusCounts);
                    config.setEndTime(endTime);
                    config.setStartTime(startTime);
                    config.setActiveTime(activeTime);
                    config.setInactiveTime(inactiveTime);
                    operaFlag = asPrizeConfigMapper.updateByPrimaryKey(config) == 1;
                    ValidatorBreakException.NOT_TRUE(operaFlag);


                    //更新物品表的时间
                    Map<String, Object> map = new HashMap<>();
                    map.put("prizeConfigId", prizeConfigId);
                    map.put("inactiveTime", inactiveTime);
                    map.put("activeTime", activeTime);
                    map.put("endTime", endTime);
                    map.put("startTime", startTime);
                    asPrizeMapper.updateTimeByConfigId(map);

                } else if (auditStatus == 0 || auditStatus == 2) {

                    timeDiff = CommonUtils.orderTimeDiff(
                            CommonUtils.getTimeMils(endTime),
                            System.currentTimeMillis());
                    ValidatorBreakException.NOT_TRUE(timeDiff != 0,
                            "结束时间必须大于当前时间");
                    timeDiff = CommonUtils.orderTimeDiff(
                            CommonUtils.getTimeMils(inactiveTime),
                            System.currentTimeMillis());
                    ValidatorBreakException.NOT_TRUE(timeDiff != 0,
                            "失效时间必须大于当前时间");

                    name = pd.getString("name");
                    categoryName = pd.getString("categoryName");
                    counts = pd.getInt("counts");
                    shopId = pd.getInt("shopId");
                    storeId = pd.getInt("storeId");
                    descp = pd.getString("descp");
                    //uploadModule1(UploadPathEnum.ACTIVITY, 1);
                    // uploadModule1(UploadPathEnum.ACTIVITY, 2);
				/*logoUrl = StringUtils.isNotBlank(pd.getString("img1")) ? pd.getString("img1")
						.toString() : config.getLogoUrl();*/
				/*
				 * backUrl = StringUtils.isNotBlank(pd.getString("img2")) ?
				 * pd.getString("img2") .toString() : config.getBackUrl();
				 */
                    if (!categoryName.equals("礼品券")) {
                        price = pd.getString("price");
                        config.setPrice(new BigDecimal(price));
                    } else {
                        config.setPrice(new BigDecimal("0"));
                    }
                    categoryId = pd.getIntegerRequired("categoryId");
                    couponTagId = pd.getIntegerRequired("couponTagId");

                    config.setName(name);
                    config.setCounts(counts);
                    config.setSurplusCounts(counts);
                    config.setShopId(shopId);
                    config.setStoreId(storeId);
                    config.setEndTime(endTime);
                    config.setStartTime(startTime);
                    config.setActiveTime(activeTime);
                    config.setInactiveTime(inactiveTime);
                    config.setDescp(descp);
                    //config.setLogoUrl(logoUrl);
                    // config.setBackUrl(backUrl);
                    config.setActivityType(ActivityTypeEnum.COUPON_TYPE.getId());
                    config.setCategoryId(categoryId);
                    operaFlag = asPrizeConfigMapper
                            .updateByPrimaryKeyWithBLOBs(config) == 1;
                    asPrizeCategory = asPrizeCategoryMapper.selectByConfigId(prizeConfigId);
                    if (asPrizeCategory != null) {
                        if (asPrizeCategory.getCategoryId() != couponTagId) {
                            // 更新优惠劵标签关系表
                            asPrizeCategory.setPrizeConfigId(prizeConfigId);
                            asPrizeCategory.setCategoryId(couponTagId);
                            operaFlag = asPrizeCategoryMapper
                                    .updateByPrimaryKey(asPrizeCategory) == 1;
                            ValidatorBreakException.NOT_TRUE(operaFlag);
                        }
                    }
                    ValidatorBreakException.NOT_TRUE(operaFlag);
                }
                break;
            default:
                ValidatorBreakException.NOT_TRUE(false);
        }

        return renderSuccess();
    }

    /**
     * 异步加载商场摇一摇中奖级别
     *
     * @return
     * @author tgb 创建时间：2016年10月26日
     * @version 1.0
     */
    public Object shake(BaseController c) {
        initialized(c);
        String shopId = pd.getString("shopId");
        List<AsActivityShake> list = new ArrayList<AsActivityShake>();
        if (StringUtils.isNotBlank(shopId)) {
            AsShop asShop = asShopMapper.findByShopId(shopId);
            if (asShop != null) {
                if (asShop.getType().equals(2)) {
                    //商户概率
                    List<AsActivityShake> shakeList = asActivityShakeMapper.selectAll();
                    for (AsActivityShake shake : shakeList) {
                        //排除名字和概率没有设置的
                        BigDecimal a = shake.getProbability();
                        BigDecimal temp = new BigDecimal(0);
                        int result = a.compareTo(temp);
                        if (StringUtils.isNotBlank(shake.getName()) && result > 0) {
                            list.add(shake);
                        }
                    }
                } else {
                    //指定商场概率
                    list = asActivityShakeMapper.fingByStoreId(asShop.getStoreId());
                }
            }
        }
        return list;
    }

    /**
     * 异步检查是否重名
     *
     * @return
     * @author tgb 创建时间：2016年10月26日
     * @version 1.0
     */
    public Object isExist(BaseController c) {
        initialized(c);
        String name = pd.getString("name");
        Integer shopId = pd.getInt("shopId");
        Integer type = pd.getInt("type");
        Integer data = asPrizeConfigMapper.findByName(name, shopId, type);
        if (data > 0) {
            return true;
        }
        return false;
    }

    /**
     * 转盘礼品历史记录
     *
     * @param c
     * @return
     * @author tgb 创建时间：2016年10月21日
     * @version 1.0
     */
    public Object listHistory(BaseController c) {

        initialized(c);
        addField("shopId", getUser().getAs_shop_id());
        pageQueryModule(asPrizeConfigMapper, "listHistory", true);
        List<AsYpBaseCategory> prizeTypeList = asYpBaseCategoryMapper
                .queryPrizeListAll(BaseCategoryTypeEnum.REDRAIN_PRIZE_TYPE
                        .getId());
        ValidatorBreakException.NOT_NULL(prizeTypeList, "获取礼品分类失败");
        ArrayList<AsYpBaseCategory> newPrizeTypeList = new ArrayList<AsYpBaseCategory>();

        // 实际兑换数 抽奖类型： 1 抽奖  2 摇奖 3 优惠劵 4 自主活动抽奖
        List<AsPrize> exchangeCountList = asPrizeMapper.getExchangeCountList(1);
        addViewData("exchangeCountList", exchangeCountList);
        addViewData("shopId", getUser().getAs_shop_id());
        if (isAdminRole()) {//系统运营管理员
            for (AsYpBaseCategory aCategory : prizeTypeList) {
                if (aCategory.getName().equals("折扣券")
                        || aCategory.getName().equals("代金券")
                        || aCategory.getName().equals("礼品券")
                        || aCategory.getName().equals("积分")) {
                    if (aCategory.getName().equals("积分")) {
                        newPrizeTypeList.add(0, aCategory);
                    } else {
                        newPrizeTypeList.add(aCategory);
                    }
                }
            }
            PageData pd = new PageData();
            List<AsProvince> provinceList = asProvinceService.list(pd);
            List<AsCity> cityList = asCityService.list(pd);
            JSONArray cityListJson = JSONArray.fromObject(cityList);
            List<AsArea> areaList = asAreaService.list(pd);
            JSONArray areaListJson = JSONArray.fromObject(areaList);

            addViewData("provinceList", provinceList);
            addViewData("cityList", cityListJson);
            addViewData("areaList", areaListJson);
        } else {//普通商户
            for (AsYpBaseCategory aCategory : prizeTypeList) {
                if (aCategory.getName().equals("折扣券")
                        || aCategory.getName().equals("代金券")
                        || aCategory.getName().equals("礼品券")) {
                    newPrizeTypeList.add(aCategory);
                }
            }
        }

        addViewData("prizeTypeList", newPrizeTypeList);
        addViewData("activityType", ActivityTypeEnum.DRAW_TYPE.getId());
        return renderView(PAGE_PATH + "listHistory");
    }

    /**
     * 转盘礼品列表
     *
     * @param c
     * @return
     * @author tgb
     * @date 2016年11月3日
     */
    public Object listAll(BaseController c) {

        initialized(c);
        addField("shopId", getUser().getAs_shop_id());
        pageQueryModule(asPrizeConfigMapper, "listAll", true);
        List<AsYpBaseCategory> prizeTypeList = asYpBaseCategoryMapper
                .queryPrizeListAll(BaseCategoryTypeEnum.REDRAIN_PRIZE_TYPE
                        .getId());
        ValidatorBreakException.NOT_NULL(prizeTypeList, "获取礼品分类失败");
        ArrayList<AsYpBaseCategory> newPrizeTypeList = new ArrayList<AsYpBaseCategory>();

        // 实际兑换数 类型 1抽奖 2摇奖 3领取 4 自主活动抽奖
        List<AsPrize> exchangeCountList = asPrizeMapper.getExchangeCountList(1);
        addViewData("exchangeCountList", exchangeCountList);
        addViewData("shopId", getUser().getAs_shop_id());
        if (isAdminRole()) {//系统运营管理员
            for (AsYpBaseCategory aCategory : prizeTypeList) {
                if (aCategory.getName().equals("折扣券")
                        || aCategory.getName().equals("代金券")
                        || aCategory.getName().equals("礼品券")
                        || aCategory.getName().equals("积分")) {
                    if (aCategory.getName().equals("积分")) {
                        newPrizeTypeList.add(0, aCategory);
                    } else {
                        newPrizeTypeList.add(aCategory);
                    }
                }
            }

            PageData pd = new PageData();
            List<AsProvince> provinceList = asProvinceService.list(pd);
            List<AsCity> cityList = asCityService.list(pd);
            JSONArray cityListJson = JSONArray.fromObject(cityList);
            List<AsArea> areaList = asAreaService.list(pd);
            JSONArray areaListJson = JSONArray.fromObject(areaList);

            addViewData("provinceList", provinceList);
            addViewData("cityList", cityListJson);
            addViewData("areaList", areaListJson);
        } else {//普通商户
            for (AsYpBaseCategory aCategory : prizeTypeList) {
                if (aCategory.getName().equals("折扣券")
                        || aCategory.getName().equals("代金券")
                        || aCategory.getName().equals("礼品券")) {
                    newPrizeTypeList.add(aCategory);
                }
            }
        }
        //权限控制
		/*if (StringUtils.isNotBlank(getUser().getROLE_ID())) {
			String  adminRoleId =asDictParamMapper.findByKeyCode(DictParam.PARAM_ADMIN_ROLE_ID);
			String  operationRoleId =asDictParamMapper.findByKeyCode(DictParam.PARAM_OPERATION_MANAGER_ROLE_ID);
			addViewData("roleId", getUser().getROLE_ID().equals(adminRoleId)||getUser().getROLE_ID().equals(operationRoleId)?"1":"0");
		}*/
        addViewData("prizeTypeList", newPrizeTypeList);
        addViewData("activityType", ActivityTypeEnum.DRAW_TYPE.getId());
        return renderView(PAGE_PATH + "listAll");
    }

    /**
     * 摇奖礼品历史记录
     *
     * @param c
     * @return
     * @author tgb 创建时间：2016年10月21日
     * @version 1.0
     */
    public Object shakeListHistory(BaseController c) {

        initialized(c);
        addField("shopId", getUser().getAs_shop_id());
        pageQueryModule(asPrizeConfigMapper, "shakeListHistory", true);
        List<AsYpBaseCategory> prizeTypeList = asYpBaseCategoryMapper
                .queryPrizeListAll(BaseCategoryTypeEnum.REDRAIN_PRIZE_TYPE
                        .getId());
        ValidatorBreakException.NOT_NULL(prizeTypeList, "获取礼品分类失败");
        ArrayList<AsYpBaseCategory> newPrizeTypeList = new ArrayList<AsYpBaseCategory>();
        for (AsYpBaseCategory aCategory : prizeTypeList) {
            if (aCategory.getName().equals("积分")
                    || aCategory.getName().equals("实物礼品")
                    || aCategory.getName().equals("现金红包")) {
                if (aCategory.getName().equals("积分")) {
                    newPrizeTypeList.add(0, aCategory);
                } else {
                    newPrizeTypeList.add(aCategory);
                }
            }
        }

        // 实际兑换数 抽奖类型： 1 抽奖  2 摇奖 3 优惠劵 4 自主活动抽奖
        List<AsPrize> exchangeCountList = asPrizeMapper.getExchangeCountList(2);
        addViewData("exchangeCountList", exchangeCountList);

        addViewData("prizeTypeList", newPrizeTypeList);
        return renderView(PAGE_PATH_SHAKE + "listHistory");
    }

    /**
     * 摇奖礼品列表
     *
     * @param c
     * @return
     * @author tgb
     * @date 2016年11月3日
     */
    public Object shakeListAll(BaseController c) {

        initialized(c);
        addField("shopId", getUser().getAs_shop_id());
        pageQueryModule(asPrizeConfigMapper, "shakeListAll", true);
        List<AsYpBaseCategory> prizeTypeList = asYpBaseCategoryMapper
                .queryPrizeListAll(BaseCategoryTypeEnum.REDRAIN_PRIZE_TYPE
                        .getId());
        ValidatorBreakException.NOT_NULL(prizeTypeList, "获取礼品分类失败");
        ArrayList<AsYpBaseCategory> newPrizeTypeList = new ArrayList<AsYpBaseCategory>();

        for (AsYpBaseCategory aCategory : prizeTypeList) {
            if (aCategory.getName().equals("积分")
                    || aCategory.getName().equals("实物礼品")
                    || aCategory.getName().equals("现金红包")) {
                if (aCategory.getName().equals("积分")) {
                    newPrizeTypeList.add(0, aCategory);
                } else {
                    newPrizeTypeList.add(aCategory);
                }
            }
        }

        //权限控制
			/*if (StringUtils.isNotBlank(getUser().getROLE_ID())) {
				String  adminRoleId =asDictParamMapper.findByKeyCode(DictParam.PARAM_ADMIN_ROLE_ID);
				String  operationRoleId =asDictParamMapper.findByKeyCode(DictParam.PARAM_OPERATION_MANAGER_ROLE_ID);
				addViewData("roleId", getUser().getROLE_ID().equals(adminRoleId)||getUser().getROLE_ID().equals(operationRoleId)?"1":"0");
			}*/

        // 实际兑换数 抽奖类型： 1 抽奖  2 摇奖 3 优惠劵 4 自主活动抽奖
        List<AsPrize> exchangeCountList = asPrizeMapper.getExchangeCountList(2);
        addViewData("exchangeCountList", exchangeCountList);

        addViewData("prizeTypeList", newPrizeTypeList);
        addViewData("activityType", ActivityTypeEnum.SHAKE_TYPE.getId());
        return renderView(PAGE_PATH_SHAKE + "listAll");
    }

    /**
     * 优惠券列表历史记录
     *
     * @param c
     * @return
     * @author tgb 创建时间：2016年10月26日
     * @version 1.0
     */
    public Object couponsListHistory(BaseController c) {

        initialized(c);
        addField("shopId", getUser().getAs_shop_id());
        addField("couponTagId", pd.getIntegerRequired("couponTagId"));
        pageQueryModule(asPrizeConfigMapper, "couponsListHistory", true);

        List<AsYpBaseCategory> prizeTypeList = asYpBaseCategoryMapper
                .queryPrizeListAll(BaseCategoryTypeEnum.REDRAIN_PRIZE_TYPE
                        .getId());
        ValidatorBreakException.NOT_NULL(prizeTypeList, "获取礼品分类失败");
        ArrayList<AsYpBaseCategory> newPrizeTypeList = new ArrayList<AsYpBaseCategory>();
        for (AsYpBaseCategory aCategory : prizeTypeList) {
            if (aCategory.getName().equals("折扣券")
                    || aCategory.getName().equals("代金券")
                    || aCategory.getName().equals("礼品券")) {
                newPrizeTypeList.add(aCategory);
            }
        }
        addViewData("shopId", getUser().getAs_shop_id());
        addViewData("prizeTypeList", newPrizeTypeList);
        pd.put("type", BaseCategoryTypeEnum.REDRAIN_COUPON_TYPE.getId());
        List<Object> tagTypeList = queryModule(asYpBaseCategoryMapper,
                "queryCouponsListAll", false);
        addViewData("tagTypeList", tagTypeList);

        // 实际兑换数 类型 1抽奖 2摇奖 3领取 4 自主活动抽奖
        List<AsPrize> exchangeCountList = asPrizeMapper.getExchangeCountList(3);
        addViewData("exchangeCountList", exchangeCountList);

        if (isAdminRole()) {
            PageData pd = new PageData();
            List<AsProvince> provinceList = asProvinceService.list(pd);
            List<AsCity> cityList = asCityService.list(pd);
            JSONArray cityListJson = JSONArray.fromObject(cityList);
            List<AsArea> areaList = asAreaService.list(pd);
            JSONArray areaListJson = JSONArray.fromObject(areaList);

            addViewData("provinceList", provinceList);
            addViewData("cityList", cityListJson);
            addViewData("areaList", areaListJson);
        }


        ValidatorBreakException.NOT_NULL(tagTypeList, 1001, "加载优惠券标签异常", 1);
        return renderView(PAGE_PATH_COUPON + "listHistory");
    }

    /**
     * 优惠券列表
     *
     * @param c
     * @return
     * @author tgb 创建时间：2016年10月26日
     * @version 1.0
     */
    public Object couponsListAll(BaseController c) {

        initialized(c);
        addField("shopId", getUser().getAs_shop_id());
        addField("couponTagId", pd.getIntegerRequired("couponTagId"));
        pageQueryModule(asPrizeConfigMapper, "couponsListAll", true);
        List<AsYpBaseCategory> prizeTypeList = asYpBaseCategoryMapper
                .queryPrizeListAll(BaseCategoryTypeEnum.REDRAIN_PRIZE_TYPE
                        .getId());
        ValidatorBreakException.NOT_NULL(prizeTypeList, "获取礼品分类失败");
        ArrayList<AsYpBaseCategory> newPrizeTypeList = new ArrayList<AsYpBaseCategory>();
        for (AsYpBaseCategory aCategory : prizeTypeList) {
            if (aCategory.getName().equals("折扣券")
                    || aCategory.getName().equals("代金券")
                    || aCategory.getName().equals("礼品券")) {
                newPrizeTypeList.add(aCategory);
            }
        }
        addViewData("shopId", getUser().getAs_shop_id());
        addViewData("prizeTypeList", newPrizeTypeList);
        pd.put("type", BaseCategoryTypeEnum.REDRAIN_COUPON_TYPE.getId());
        List<Object> tagTypeList = queryModule(asYpBaseCategoryMapper,
                "queryCouponsListAll", false);
        addViewData("tagTypeList", tagTypeList);
        ValidatorBreakException.NOT_NULL(tagTypeList, 1001, "加载优惠券标签异常", 1);

        // 实际兑换数 类型 1抽奖 2摇奖 3领取 4 自主活动抽奖
        List<AsPrize> exchangeCountList = asPrizeMapper.getExchangeCountList(3);
        addViewData("exchangeCountList", exchangeCountList);

        if (isAdminRole()) {
            PageData pd = new PageData();
            List<AsProvince> provinceList = asProvinceService.list(pd);
            List<AsCity> cityList = asCityService.list(pd);
            JSONArray cityListJson = JSONArray.fromObject(cityList);
            List<AsArea> areaList = asAreaService.list(pd);
            JSONArray areaListJson = JSONArray.fromObject(areaList);

            addViewData("provinceList", provinceList);
            addViewData("cityList", cityListJson);
            addViewData("areaList", areaListJson);
        }

        //权限控制
		/*if (StringUtils.isNotBlank(getUser().getROLE_ID())) {
			String  adminRoleId =asDictParamMapper.findByKeyCode(DictParam.PARAM_ADMIN_ROLE_ID);
			String  operationRoleId =asDictParamMapper.findByKeyCode(DictParam.PARAM_OPERATION_MANAGER_ROLE_ID);
			addViewData("roleId", getUser().getROLE_ID().equals(adminRoleId)||getUser().getROLE_ID().equals(operationRoleId)?"1":"0");
		}*/

        return renderView(PAGE_PATH_COUPON + "listAll");
    }

    /**
     * 查看驳回理由
     *
     * @param c
     * @return
     * @author tgb 创建时间：2016年10月25日
     * @version 1.0
     */
    public Object reason(BaseController c) {

        initialized(c);

        int prizeConfigId = pd.getInt("prizeConfigId");
        AsPrizeConfig config = (AsPrizeConfig) asPrizeConfigMapper
                .selectByPrimaryKey(prizeConfigId);
        ValidatorBreakException.NOT_NULL(config, 1001, "系统繁忙", 1);
        addViewData("config", config);
        return renderView(PAGE_PATH + "reason");
    }

    /**
     * 活动统计
     *
     * @return
     * @author tgb 创建时间：2016年11月1日
     * @version 1.0
     */
    public Object info(BaseController c) {

        initialized(c);
        List<Map<String, Object>> infoList = asPrizeConfigMapper.info(getUser()
                .getAs_shop_id(), DateUtil.getCurMonth());
        ValidatorBreakException.NOT_NULL(infoList, 1001, "统计信息数据异常", 1);
        addViewData("infoList", infoList);
        return renderView(PAGE_PATH + "info");
    }

    /**
     * 更新礼品状态
     *
     * @param c
     * @return
     * @author tgb 创建时间：2016年10月28日
     * @version 1.0
     */
    public Object updatePrizeStatus(BaseController c) {

        initialized(c);

        int prizeConfigId = pd.getInt("prizeConfigId");
        int status = pd.getInt("status");
        operaFlag = asPrizeConfigMapper
                .updatePrizeStatus(prizeConfigId, status) == 1;
        ValidatorBreakException.NOT_TRUE(operaFlag);
        return renderSuccess();
    }

    /**
     * 审核列表
     *
     * @param c
     * @return
     * @author chenwenzhu
     * @date 2016年10月25日
     */
    public Object listAudit(BaseController c) {
        initialized(c);
        pageQueryModule(asPrizeConfigMapper, "listAudit", true);
        return renderView(PAGE_PATH + "listAudit");

    }

    /**
     * 审核处理
     *
     * @param c
     * @return
     * @author chenwenzhu
     * @date 2016年10月25日
     */

    public Object operpage(BaseController c) {
        initialized(c);
        Conditions prize = asPrizeConfigMapper.getPrize(pd);
        addViewData("pd", prize);
        return renderView(PAGE_PATH + "auditEdit");
    }

    /**
     * 更新审核状态
     *
     * @param c
     * @return
     * @author chenwenzhu
     * @date 2016年10月25日
     */

    public Object prizeAuditStatus(BaseController c) {
        initialized(c);
        int prizeConfigId = pd.getInt("prizeConfigId");
        int auditStatus = pd.getInt("auditStatus");
        int shopId = pd.getInt("shopId");
        String reason = pd.getString("reason");
        AsPrizeConfig prize = asPrizeConfigMapper.selectByPrimaryKey(prizeConfigId);
        if (auditStatus == 1) {
            prize.setPrizeConfigId(prizeConfigId);
            prize.setAuditStatus(auditStatus);
            prize.setAuditTime(DateUtil.getTime());
            prize.setStatus(1);
            prize.setReason(reason);
            operaFlag = asPrizeConfigMapper.updateByPrimaryKeySelective(prize) == 1;
            ValidatorBreakException.NOT_TRUE(operaFlag);
            //如果是优惠券  审核通过后推送给该商户下的会员
            if(prize.getActivityType().equals(3)){
                List<AsShopUserRelate> shopUserRelates = asShopUserRelateMapper.selectListRelateByShopId(shopId);
                if(!shopUserRelates.isEmpty()&&shopUserRelates.size()>0){
                    for (AsShopUserRelate relate : shopUserRelates) {
                        AsPrizeConfigPush push = new AsPrizeConfigPush();
                        push.setPrizeConfigId(prizeConfigId);
                        push.setUserId(relate.getUserId());
                        operaFlag = asPrizeConfigPushMapper.insertSelective(push) == 1;
                        ValidatorBreakException.NOT_TRUE(operaFlag);
                    }
                }
            }

        } else if (auditStatus == 2) {
            prize.setPrizeConfigId(prizeConfigId);
            prize.setAuditStatus(auditStatus);
            prize.setAuditTime(DateUtil.getTime());
            prize.setReason(reason);
            operaFlag = asPrizeConfigMapper.updateByPrimaryKeySelective(prize) == 1;
            ValidatorBreakException.NOT_TRUE(operaFlag);
        }
        return renderSuccess(1);

    }

    /**
     * 审核历史列表
     *
     * @param c
     * @return
     * @author chenwenzhu
     * @date 2016年10月25日
     */

    public Object listAuditHistory(BaseController c) {

        initialized(c);
        pageQueryModule(asPrizeConfigMapper, "listAuditHistory", true);
        return renderView(PAGE_PATH + "listAuditHistory");
    }


    /**
     * 批量新审核状态
     *
     * @return
     * @author wangjs
     * @date 2017年02月21日
     */
    public Object prizeAllAuditStatus(String[] ids, Integer auditStatus) {
        Map<String, Object> data = new HashMap<String, Object>();
        //List<String> errIds = new ArrayList<>();
        String errIds = "";

        if (ids.length > 0) {
            for (int i = 0; i < ids.length; i++) {
                Integer prizeConfigId = Integer.valueOf(ids[i]);
                AsPrizeConfig prize = asPrizeConfigMapper.selectByPrimaryKey(prizeConfigId);
                if (auditStatus == 1) {
                    prize.setAuditStatus(auditStatus);
                    prize.setAuditTime(DateUtil.getTime());
                    prize.setStatus(1);
                    prize.setReason("审核通过");
                    operaFlag = asPrizeConfigMapper.updateByPrimaryKeySelective(prize) == 1;
                    if (!operaFlag) {
                        errIds = prizeConfigId.toString() + ",";
                    }
                    Integer shopId = prize.getShopId();
                    //如果是优惠券 审核通过后推送给该商户下的会员
                    if(prize.getActivityType().equals(3)){
                        List<AsShopUserRelate> shopUserRelates = asShopUserRelateMapper.selectListRelateByShopId(shopId);
                        if(!shopUserRelates.isEmpty()&&shopUserRelates.size()>0){
                            for (AsShopUserRelate relate : shopUserRelates) {
                                AsPrizeConfigPush push = new AsPrizeConfigPush();
                                push.setPrizeConfigId(prizeConfigId);
                                push.setUserId(relate.getUserId());
                                operaFlag = asPrizeConfigPushMapper.insertSelective(push) == 1;
                                if (!operaFlag) {
                                    errIds = prizeConfigId.toString() + ",";
                                }
                            }
                        }
                    }
                } else if (auditStatus == 2) {
                    prize.setPrizeConfigId(prizeConfigId);
                    prize.setAuditStatus(auditStatus);
                    prize.setAuditTime(DateUtil.getTime());
                    prize.setReason("审核不通过");
                    operaFlag = asPrizeConfigMapper.updateByPrimaryKeySelective(prize) == 1;
                    ValidatorBreakException.NOT_TRUE(operaFlag);
                    if (!operaFlag) {
                        errIds = prizeConfigId.toString() + ",";
                    }
                }
            }
            data.put("status", "0");
            data.put("errIds", errIds);

        } else {
            data.put("status", "1");
        }

        return data;
    }


    /**
     * 查看历史详情
     *
     * @return
     * @author wangjs
     * @date 2017年07月21日
     */

    public Object listAuditHistoryByCfgId(BaseController c, String prizeConfigById) {

        initialized(c);

        //Integer prizeConfigId =Integer.valueOf(pd.getString("prizeConfigId"));
        Integer prizeConfigByIds = Integer.valueOf(prizeConfigById);
        Conditions conditions = asPrizeConfigMapper.listAuditHistoryByCfgId(prizeConfigByIds);

        addViewData("asPrizeConfig", conditions);

        //addViewData("asPrizeConfig",asPrizeConfig);
        return renderView(PAGE_PATH + "auditHistoryInfo");
    }

    /**
     * 判断是否是管理员和运营管理组权限
     *
     * @return
     */
    public boolean isAdminRole() {
        String roleId = getUser().getROLE_ID();
        if (StringUtils.isNotBlank(roleId)) {
            String adminRoleId = asDictParamMapper.findByKeyCode(DictParam.PARAM_ADMIN_ROLE_ID);
            String operationRoleId = asDictParamMapper.findByKeyCode(DictParam.PARAM_OPERATION_MANAGER_ROLE_ID);
            if (roleId.equals(adminRoleId) || roleId.equals(operationRoleId)) {
                return true;
            }
        }
        return false;
    }

}
