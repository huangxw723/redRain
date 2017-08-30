package com.aoshi.service.manage;

import com.aoshi.common.asenum.*;
import com.aoshi.controller.base.BaseController;
import com.aoshi.dao.*;
import com.aoshi.domain.*;
import com.aoshi.resolver.ValidatorBreakException;
import com.aoshi.service.base.AsBaseService;
import com.aoshi.util.*;
import net.sf.json.JSONArray;
import org.apache.commons.lang.StringUtils;
import org.apache.shiro.codec.Base64;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 帐号管理
 *
 * @author yangyanchao
 * @date 2016年10月27日
 */
@Service
public class AccountService extends AsBaseService {

    private final static String PAGE_PATH = "manage/account/";

    @Autowired
    private AsUserMapper asUserMapper;

    @Autowired
    private AsShopOwnerInfoMapper asShopOwnerInfoMapper;

    @Autowired
    private AsShopStaffInfoMapper asShopStaffInfoMapper;

    @Autowired
    private AsShopMapper asShopMapper;

    @Autowired
    private AsYpScreenDomainMapper asYpScreenDomainMapper;

    @Autowired
    private AsDictParamMapper asDictParamMapper;

    @Autowired
    private SysUserMapper sysUserMapper;

    @Autowired
    private AsWalletMapper asWalletMapper;

    @Autowired
    private AsPrizeConfigMapper asPrizeConfigMapper;

    @Autowired
    private AsAdminInfoMapper asAdminInfoMapper;

    @Autowired
    private AsProvinceMapper provinceMapper;

    @Autowired
    private AsCityMapper asCityMapper;

    @Autowired
    private AsAreaMapper asAreaMapper;

    @Autowired
    private AsBusinessDistrictMapper asBusinessDistrictMapper;

    @Autowired
    private AsStoreMapper asStoreMapperr;

    @Autowired
    private AsYpBaseCategoryMapper ypBaseCategoryMapper;

    @Autowired
    private AsYpShopAreaMapper ypShopAreaMapper;

    @Autowired
    private AsShopStaffInfoMapper shopStaffInfoMapper;

    @Autowired
    private AsShopSpellMapper asShopSpellMapper;

    @Autowired
    private SysRoleMapper sysRoleMapper;

    @Autowired
    private AsUserAddressMapper userAddressMapper;

    @Autowired
    private AsBankBindMapper bankBindMapper;

    @Autowired
    private AsUserInfoMapper userInfoMapper;

    @Autowired
    private AsCapitalMapper capitalMapper;

    @Autowired
    private AsUserPointMapper userPointMapper;

    @Autowired
    private AsShopPicMapper shopPicMapper;



    /**
     * 跳转到添加商户账号页面（新）
     *
     * @param c
     * @return
     * @author tgb
     * @date 2016年10月31日
     */
    public Object saveAccountPage(BaseController c) {

        initialized(c);
        OperaEnum action = OperaEnum.get(pd.getInt("action") == 0 ? 1 : pd.getInt("action"));
        switch (action) {
            case ADD:
                 break;
            default:
                ValidatorBreakException.NOT_NULL(null);
        }
        addField("action", action.getId());
        return renderView(PAGE_PATH + "saveAccountPage");
    }

    /**
     * 异步检查商家账号是否重名
     *
     * @return
     * @author tgb 创建时间：2016年10月26日
     * @version 1.0
     */
    public Object isExist(BaseController c) {
        initialized(c);
        String account = pd.getParaForString("account");
        //判断是否是管理员和澳视商户账号
        if("admin".equals(account)||"ascmMallManager".equals(account)){
            return true;
        }
        int data = asUserMapper.isAccountExists(Conditions.newInstance().putData("account", account).putData("type", UserTypeEnum.SELLER.getId()));
        if (data > 0) {
            return true;
        }
        return false;
    }

    /**
     * 异步检查预留手机号码是否已经被使用
     *
     * @return
     * @author tgb 创建时间：2016年10月26日
     * @version 1.0
     */
    public Object isExistPhone(BaseController c) {
        initialized(c);
        String phone = pd.getParaForString("phone");
        int data = asUserMapper.isAccountExistsCheck(Conditions.newInstance().putData("phone", phone).putData("type", UserTypeEnum.SELLER.getId()));
        if (data > 0) {
            return true;
        }
        return false;
    }

    /**
     * 异步检查商户名称是否重名
     *
     * @return
     * @author tgb 创建时间：2016年10月26日
     * @version 1.0
     */
    public Object isMerchantAccountExists(BaseController c) {
        initialized(c);
        String name = pd.getParaForString("name");
        Integer shopId = pd.getInt("shopId");
        int data =  asShopMapper.isMerchantAccountExists(Conditions.newInstance().putData("name", name).putData("shopId", shopId));
        if (data > 0) {
            return true;
        }
        return false;
    }

    /**
     * 保存商户帐号操作（新）
     * @param c
     * @return
     */
    public Object saveAccountOpera(BaseController c) {
        initialized(c);

        String password = pd.getParaForString("password");
        String confirmPassword = pd.getParaForString("confirmPassword");
        ValidatorBreakException.NOT_TRUE(password.equals(confirmPassword), "两次密码输入不一样");
        String account = pd.getParaForString("account");
        String phone = pd.getParaForString("phone");
        Integer userType = pd.getInt("type");

        //判断是否是管理员和澳视商户账号
        if("admin".equals(account)||"ascmMallManager".equals(account)){
            ValidatorBreakException.NOT_TRUE(false, "用户名已被注册！");
        }

        //检查商家账号
        operaFlag = asUserMapper.isAccountExists(Conditions.newInstance().putData("account", account).putData("type", UserTypeEnum.SELLER.getId())) > 0;
        ValidatorBreakException.NOT_TRUE(!operaFlag, "用户名已被注册！");
        //后台用户名检查
        operaFlag = sysUserMapper.userNameIsExist(Conditions.newInstance().putData("account", account).putData("type", UserTypeEnum.SELLER.getId())) > 0;
        ValidatorBreakException.NOT_TRUE(!operaFlag, "用户名已被注册！");
        //检查预留手机号码是否已经被使用
        operaFlag = asUserMapper.isAccountExistsCheck(Conditions.newInstance().putData("phone", phone).putData("type", UserTypeEnum.SELLER.getId())) > 0;
        ValidatorBreakException.NOT_TRUE(!operaFlag, "该手机号码已被使用!");
        //商场创建
        Conditions cd = Conditions.newInstance().putData("type", userType);
        operaFlag = asShopMapper.insertSelective(cd) == 1;

        //创建后台用户
        String sys_user_id = UuidUtil.get32UUID();
        Conditions sysUser = Conditions.newInstance();
        sysUser.putData("username", account).putData("name", account).putData("userId", sys_user_id).putData("phone", phone)
                .putData("password", CommonUtils.encryptPwd(password, 2)).putData("asShopId", cd.getInt("shopId")).putData("status", 0)
                .putData("skin", "skin-2").putData("roleId", asDictParamMapper.findByKeyCode(userType.compareTo(1)==0?
                DictParam.PARAM_MERCHANTSHOPOWNER_ROLE_ID:userType.compareTo(2)==0?DictParam.PARAM_SHOPOWNER_ROLE_ID:DictParam.PARAM_MERCHANTSHOPOWNER_ROLE_ID))
                .put("GROUP_ID", "");
        operaFlag = sysUserMapper.insertSelective(sysUser) == 1;
        ValidatorBreakException.NOT_TRUE(operaFlag);

        //创建账号
        Conditions user = Conditions.newInstance();
        user.putData("account", account).putData("password", CommonUtils.encryptPwd(password)).putData("createTime", CommonUtils.getCurDate())
                .putData("type", UserTypeEnum.SELLER.getId()).putData("sysUserId", sys_user_id);
        operaFlag = asUserMapper.insertSelective(user) == 1;
        ValidatorBreakException.NOT_TRUE(operaFlag);


        //创建商户店铺管理员信息
        Conditions shopOwner = Conditions.newInstance();
        shopOwner.putData("userId", user.getInt("userId")).putData("shopId", cd.getInt("shopId")).putData("name", "")
                .putData("status", 0)
                .putData("tel", "")
                .putData("email", "")
                .putData("createTime", DateUtil.getTime());
        operaFlag = asShopOwnerInfoMapper.insertSelective(shopOwner) == 1;
        ValidatorBreakException.NOT_TRUE(operaFlag);

        //添加用户钱包
        AsWallet wallet = new AsWallet();
        wallet.setUserId(user.getInt("userId"));
        wallet.setCreateTime(CommonUtils.getCurDate());
        operaFlag = asWalletMapper.insertSelective(wallet) == 1;
        ValidatorBreakException.NOT_TRUE(operaFlag);

        // 添加员工帐号信息
        Conditions staffUser = Conditions.newInstance();
        staffUser.putData("account", account).putData("password", CommonUtils.encryptPwd(password)).putData("createTime", CommonUtils.getCurDate())
                .putData("type", UserTypeEnum.STAFF.getId());
        operaFlag = asUserMapper.insertSelective(staffUser) == 1;
        ValidatorBreakException.NOT_TRUE(operaFlag);

        // 添加员工信息
        AsShopStaffInfo staffInfo = new AsShopStaffInfo();
        staffInfo.setUserId(staffUser.getInt("userId"));
        staffInfo.setShopUserId(shopOwner.getInt("userId"));
        staffInfo.setShopId(cd.getInt("shopId"));
        staffInfo.setDescp("");
        staffInfo.setName("");
        staffInfo.setSn(UuidUtil.get32UUID());
        staffInfo.setTel("");
        staffInfo.setEmail("");
        operaFlag = asShopStaffInfoMapper.insertSelective(staffInfo) == 1;
        ValidatorBreakException.NOT_TRUE(operaFlag);

        // 添加员工钱包
        AsWallet staffWallet = new AsWallet();
        staffWallet.setUserId(staffUser.getInt("userId"));
        staffWallet.setCreateTime(CommonUtils.getCurDate());
        operaFlag = asWalletMapper.insertSelective(staffWallet) == 1;
        ValidatorBreakException.NOT_TRUE(operaFlag);

        return renderSuccess();
    }

    /**
     * 获取当前登录用户商家个人信息页面-可编辑
     *
     * @return c
     * @author tgb
     * @date 2017年02月21日
     */
    public Object ownerInfoPage(BaseController c) {

        initialized(c);

        OperaEnum action = OperaEnum.get(pd.getInt("action") == 0 ? 1 : pd.getInt("action"));
        switch (action) {

            case EDIT:
                Integer shopId = pd.getInt("shopId");
                if(shopId ==0){
                    String tempShopId = getUser().getAs_shop_id();
                    shopId = Integer.parseInt(tempShopId);
                }
                Object obj =  asShopOwnerInfoMapper.getOwnerInfoByShopId(shopId);
                addViewData("shopOwner",obj);
                addViewData("msg","operaOwner");
                break;
            case QUERY:
                shopId = pd.getInt("shopId");
                if(shopId ==0){
                    String tempShopId = getUser().getAs_shop_id();
                    shopId = Integer.parseInt(tempShopId);
                }
                obj =  asShopOwnerInfoMapper.getOwnerInfoByShopId(shopId);
                addViewData("shopOwner",obj);
                break;
            default:
                ValidatorBreakException.NOT_NULL(null);
        }
        addViewData("action", action.getId());
        return renderView(PAGE_PATH + "ownerInfo");
    }

    /**
     * 获取当前登录用户商家信息页面-可编辑
     *
     * @return c
     * @author tgb
     * @date 2017年02月21日
     */
    public Object shopInfoPage(BaseController c) {

        initialized(c);

        OperaEnum action = OperaEnum.get(pd.getInt("action") == 0 ? 1 : pd.getInt("action"));
        switch (action) {
            case ADD:
                List<AsProvince> provinceList = provinceMapper.selectByAttr(pd);
                List<AsCity> cityList = asCityMapper.selectByAttr(null);
                JSONArray cityListJson = JSONArray.fromObject(cityList);
                List<AsArea> areaList = asAreaMapper.selectByAttr(null);
                JSONArray areaListJson = JSONArray.fromObject(areaList);
                List<AsBusinessDistrict> businessDistrictList = asBusinessDistrictMapper.selectByAttr(null);
                JSONArray businessDistrictListJson = JSONArray.fromObject(businessDistrictList);

                List<AsStore> storList = asStoreMapperr.getStorList(null);
                JSONArray storListJson = JSONArray.fromObject(storList);
                addField("type", baseTypeEnum.INDUSTRY.getId());
                List<Conditions> baseTypes = ypBaseCategoryMapper.queryListAll(pd);
                Map map = new HashMap<>();
                map.put("type", 1);
                Object shopOwner = map;
                addViewData("shopOwner", shopOwner);
                addViewData("provinceList", provinceList);
                addViewData("cityList", cityListJson);
                addViewData("areaList", areaListJson);
                addViewData("businessDistrictList", businessDistrictListJson);
                addViewData("storList", storListJson);
                addViewData("baseTypes", baseTypes);
                addViewData("pd", pd);
                addField("otype", 1);
                addViewData("msg", "opera");
                Object floorList = asDictParamMapper.findByKeyCode(DictParam.PARAM_SHOP_FLOOR_OPT);
                if (floorList != null) {
                    floorList = JSONArray.fromObject(floorList);
                }
                addViewData("floorList", floorList);

                break;
            case EDIT:
                Integer shopId = pd.getInt("shopId");
                if(shopId ==0){
                    String tempShopId = getUser().getAs_shop_id();
                    shopId = Integer.parseInt(tempShopId);
                }
                addField("shopId", shopId);
                shopOwner = asShopMapper.getShop(pd);
                AsShop asShop =  asShopMapper.findByShopId(shopId+"");
                List<AsShopPic> shopPics = shopPicMapper.selectByShopId(shopId);
                provinceList = provinceMapper.selectByAttr(null);
                cityList = asCityMapper.selectByAttr(null);
                cityListJson = JSONArray.fromObject(cityList);
                areaList = asAreaMapper.selectByAttr(null);
                areaListJson = JSONArray.fromObject(areaList);
                businessDistrictList = asBusinessDistrictMapper.selectByAttr(null);
                businessDistrictListJson = JSONArray.fromObject(businessDistrictList);
                storList = asStoreMapperr.getStorList(null);
                storListJson = JSONArray.fromObject(storList);
                //商场的类型
                if (asShop.getType()==1){
                    addField("type", baseTypeEnum.STORE.getId());
                }if(asShop.getType()==2){
                //商户的行业类型
                    addField("type", baseTypeEnum.INDUSTRY.getId());
                }
                baseTypes = ypBaseCategoryMapper.queryListAll(pd);
                //判断商户背景图片
                if(!shopPics.isEmpty()&&shopPics.size()>0){
                    addViewData("picUrl3", shopPics.get(0).getPicUrl());
                    if(shopPics.size()>1){
                        addViewData("picUrl4", shopPics.get(1).getPicUrl());
                        addViewData("shopPicId4", shopPics.get(1).getShopPicId());
                    }
                    if(shopPics.size()>2){
                        addViewData("picUrl5", shopPics.get(2).getPicUrl());
                        addViewData("shopPicId5", shopPics.get(2).getShopPicId());
                    }
                }
                addViewData("provinceList", provinceList);
                addViewData("cityList", cityListJson);
                addViewData("areaList", areaListJson);
                addViewData("businessDistrictList", businessDistrictListJson);
                addViewData("storList", storListJson);
                addViewData("baseTypes", baseTypes);
                addField("otype", 1);
                addViewData("shopOwner", shopOwner);
                floorList = asDictParamMapper.findByKeyCode(DictParam.PARAM_SHOP_FLOOR_OPT);
                if (floorList != null) {
                    floorList = JSONArray.fromObject(floorList);
                }
                addViewData("floorList", floorList);
                addViewData("msg", "operaShop");
                addViewData("pd", pd);
                break;
            case QUERY:
                shopId = pd.getInt("shopId");
                if(shopId ==0){
                    String tempShopId = getUser().getAs_shop_id();
                    shopId = Integer.parseInt(tempShopId);
                }
                addField("shopId", shopId);
                shopOwner = asShopMapper.getShop(pd);
                asShop =  asShopMapper.findByShopId(shopId+"");
                shopPics = shopPicMapper.selectByShopId(shopId);
                provinceList = provinceMapper.selectByAttr(null);
                cityList = asCityMapper.selectByAttr(null);
                cityListJson = JSONArray.fromObject(cityList);
                areaList = asAreaMapper.selectByAttr(null);
                areaListJson = JSONArray.fromObject(areaList);
                storList = asStoreMapperr.getStorList(null);
                storListJson = JSONArray.fromObject(storList);
                //商场的类型
                if (asShop.getType()==1){
                    addField("type", baseTypeEnum.STORE.getId());
                }if(asShop.getType()==2){
                //商户的行业类型
                    addField("type", baseTypeEnum.INDUSTRY.getId());
                }
                baseTypes = ypBaseCategoryMapper.queryListAll(pd);
                businessDistrictList = asBusinessDistrictMapper.selectByAttr(null);
                businessDistrictListJson = JSONArray.fromObject(businessDistrictList);
                addViewData("shopPics", shopPics);
                addViewData("provinceList", provinceList);
                addViewData("cityList", cityListJson);
                addViewData("areaList", areaListJson);
                addViewData("businessDistrictList", businessDistrictListJson);
                addViewData("storList", storListJson);
                addViewData("baseTypes", baseTypes);
                addViewData("shopOwner", shopOwner);
                floorList = asDictParamMapper.findByKeyCode(DictParam.PARAM_SHOP_FLOOR_OPT);
                if (floorList != null) {
                    floorList = JSONArray.fromObject(floorList);
                }
                addViewData("msg", "operaShop");
                addViewData("floorList", floorList);
                addViewData("pd", pd);
                break;
            default:
                ValidatorBreakException.NOT_NULL(null);
        }
        addViewData("action", action.getId());
        return renderView(PAGE_PATH + "saveShop");
    }

    /**
     * 查看获取商家个人信息页面
     *
     * @return c
     * @author tgb
     * @date 2017年02月21日
     */
    public Object getShopOwnerInfoPage(BaseController c) {

        initialized(c);

        OperaEnum action = OperaEnum.get(pd.getInt("action") == 0 ? 1 : pd.getInt("action"));
        switch (action) {

            case QUERY:
                int shopId = pd.getInt("shopId");
                AsShopOwnerInfo shopOwner =  asShopOwnerInfoMapper.getOwnerInfoByShopId(shopId);
                addViewData("shopOwner",shopOwner);
                break;
            default:
                ValidatorBreakException.NOT_NULL(null);
        }
        addViewData("action", action.getId());
        return renderView(PAGE_PATH + "viewOwnerInfo");
    }

    /**
     * 页面中转控制
     *
     * @return c
     * @author huangxw
     * @date 2017年02月21日
     */
    public Object page(BaseController c) {

        initialized(c);

        OperaEnum action = OperaEnum.get(pd.getInt("action") == 0 ? 1 : pd.getInt("action"));
        switch (action) {
            case ADD:
                List<AsProvince> provinceList = provinceMapper.selectByAttr(pd);
                List<AsCity> cityList = asCityMapper.selectByAttr(null);
                JSONArray cityListJson = JSONArray.fromObject(cityList);
                List<AsArea> areaList = asAreaMapper.selectByAttr(null);
                JSONArray areaListJson = JSONArray.fromObject(areaList);
                List<AsBusinessDistrict> businessDistrictList = asBusinessDistrictMapper.selectByAttr(null);
                JSONArray businessDistrictListJson = JSONArray.fromObject(businessDistrictList);

                List<AsStore> storList = asStoreMapperr.getStorList(null);
                JSONArray storListJson = JSONArray.fromObject(storList);
                addField("type", baseTypeEnum.INDUSTRY.getId());
                List<Conditions> baseTypes = ypBaseCategoryMapper.queryListAll(pd);
                Map map = new HashMap<>();
                map.put("type", 1);
                Object shopOwner = map;
                addViewData("shopOwner", shopOwner);
                addViewData("provinceList", provinceList);
                addViewData("cityList", cityListJson);
                addViewData("areaList", areaListJson);
                addViewData("businessDistrictList", businessDistrictListJson);
                addViewData("storList", storListJson);
                addViewData("baseTypes", baseTypes);
                addViewData("pd", pd);
                addField("otype", 1);
                addViewData("msg", "opera");
                Object floorList = asDictParamMapper.findByKeyCode(DictParam.PARAM_SHOP_FLOOR_OPT);
                if (floorList != null) {
                    floorList = JSONArray.fromObject(floorList);
                }
                addViewData("floorList", floorList);
                break;
            case EDIT:
                shopOwner = asShopMapper.getShop(pd);
                int shopId = pd.getInt("shopId");
                int type = pd.getInt("type");
                List<AsShopPic> shopPics = shopPicMapper.selectByShopId(shopId);
                provinceList = provinceMapper.selectByAttr(null);
                cityList = asCityMapper.selectByAttr(null);
                cityListJson = JSONArray.fromObject(cityList);
                areaList = asAreaMapper.selectByAttr(null);
                areaListJson = JSONArray.fromObject(areaList);
                businessDistrictList = asBusinessDistrictMapper.selectByAttr(null);
                businessDistrictListJson = JSONArray.fromObject(businessDistrictList);
                storList = asStoreMapperr.getStorList(null);
                storListJson = JSONArray.fromObject(storList);
                //商场的类型
                if (type==1){
                    addField("type", baseTypeEnum.STORE.getId());
                }if(type==2){
                //商户的行业类型
                    addField("type", baseTypeEnum.INDUSTRY.getId());
                }
                baseTypes = ypBaseCategoryMapper.queryListAll(pd);
                addViewData("provinceList", provinceList);
                addViewData("cityList", cityListJson);
                addViewData("areaList", areaListJson);
                addViewData("businessDistrictList", businessDistrictListJson);
                addViewData("storList", storListJson);
                addViewData("baseTypes", baseTypes);
                addViewData("msg", "opera");
                addField("otype", 1);
                addViewData("shopOwner", shopOwner);
                floorList = asDictParamMapper.findByKeyCode(DictParam.PARAM_SHOP_FLOOR_OPT);
                if (floorList != null) {
                    floorList = JSONArray.fromObject(floorList);
                }
                addViewData("msg", "opera");
                addViewData("floorList", floorList);
                addViewData("pd", pd);
                break;
            case QUERY:
                shopOwner = asShopMapper.getShop(pd);
                shopId = pd.getInt("shopId");
                type = pd.getInt("type");
                shopPics = shopPicMapper.selectByShopId(shopId);
                provinceList = provinceMapper.selectByAttr(null);
                cityList = asCityMapper.selectByAttr(null);
                cityListJson = JSONArray.fromObject(cityList);
                areaList = asAreaMapper.selectByAttr(null);
                areaListJson = JSONArray.fromObject(areaList);
                storList = asStoreMapperr.getStorList(null);
                storListJson = JSONArray.fromObject(storList);
                //商场的类型
                if (type==1){
                    addField("type", baseTypeEnum.STORE.getId());
                }if(type==2){
                //商户的行业类型
                    addField("type", baseTypeEnum.INDUSTRY.getId());
                }
                baseTypes = ypBaseCategoryMapper.queryListAll(pd);
                businessDistrictList = asBusinessDistrictMapper.selectByAttr(null);
                businessDistrictListJson = JSONArray.fromObject(businessDistrictList);
                addViewData("shopPics", shopPics);
                addViewData("provinceList", provinceList);
                addViewData("cityList", cityListJson);
                addViewData("areaList", areaListJson);
                addViewData("businessDistrictList", businessDistrictListJson);
                addViewData("storList", storListJson);
                addViewData("baseTypes", baseTypes);
                addViewData("shopOwner", shopOwner);
                floorList = asDictParamMapper.findByKeyCode(DictParam.PARAM_SHOP_FLOOR_OPT);
                if (floorList != null) {
                    floorList = JSONArray.fromObject(floorList);
                }
                addViewData("msg", "opera");
                addViewData("floorList", floorList);
                addViewData("pd", pd);
                break;
            default:
                ValidatorBreakException.NOT_NULL(null);
        }
        addViewData("action", action.getId());
        return renderView(PAGE_PATH + "save");
    }

    /**
     * 操作编辑当前登录商户帐号信息
     *
     * @return c
     * @author tgb
     * @date 2017年02月21日
     */
    public Object operaShop(BaseController c) {

        initialized(c);

        OperaEnum action = OperaEnum.get(pd.getInt("action"));

        switch (action) {
            case ADD:
                String vshopName = (String) pd.get("vshopName");
                String shopName = pd.getStringRequired("shopName");
                String name = "";
                Integer floor = 0;
                Integer tradeCategoryId = 0;
                int provinceId = 0;
                int cityId = 0;
                int areaId = 0;
                int businessDistrictId = 0;
                String businessDistrict = "";
                String address = "";
                Integer storeId = 0;
                int type = pd.getInt("shopType");
                String lng = pd.getStringRequired("lng");
                String lat = pd.getStringRequired("lat");
                String skin = "";
                String region = null;
                if (vshopName != null && !("".equals(vshopName)) && type == 2) {
                    name = vshopName;
                    tradeCategoryId = Integer.parseInt(pd.getString("tradeCategoryId"));
                    floor = pd.getInt("floor");
                    provinceId = pd.getInt("provinceId");
                    cityId = pd.getInt("cityId");
                    areaId = pd.getInt("areaId");
                    String vid = "".equals(pd.get("storeId").toString()) ? "0" : pd.get("storeId").toString();
                    storeId = Integer.parseInt(vid);
                    businessDistrict = pd.getString("businessDistrictId");
                    String DistrictId = businessDistrict == null || "".equals(businessDistrict) ? "0" : businessDistrict;
                    businessDistrictId = Integer.parseInt(DistrictId);
                    address = pd.getStringRequired("address");
                    region = pd.getStringRequired("region");
                    skin = "skin-2";
                }
                if (shopName != null && !("".equals(shopName)) && type == 1) {
                    name = shopName.substring(0, shopName.indexOf("_"));
                    storeId = Integer.parseInt(shopName.substring(shopName.indexOf("_") + 1, shopName.length()));
                    Map<String, String> ojb = (Map) asStoreMapperr.getAddress(storeId);
                    provinceId = CommonUtils.o2i(ojb.get("provinceId"));
                    cityId = CommonUtils.o2i(ojb.get("cityId"));
                    areaId = CommonUtils.o2i(ojb.get("areaId"));
                    lng = String.valueOf(ojb.get("lng"));
                    lat = String.valueOf(ojb.get("lat"));
                    address = String.valueOf(ojb.get("address"));
                    businessDistrictId = CommonUtils.o2i(ojb.get("businessDistrictId"));
                    skin = "skin-2";
                }

                String contact = pd.getStringRequired("contact");
                String tel = pd.getStringRequired("tel");
                String contactMail = pd.getStringRequired("contactMail");
                String logoUrl = pd.get("logoUrl") == null ? "" : pd.get("logoUrl").toString();

                String account = pd.getStringRequired("account");
                String password = pd.getStringRequired("password");

                Conditions cd = Conditions.newInstance().putData("type", type).putData("name", name).putData("provinceId", provinceId).putData("cityId", cityId)
                        .putData("areaId", areaId).putData("businessDistrictId", businessDistrictId).putData("tradeCategoryId", tradeCategoryId)
                        .putData("floor", floor).putData("address", address).putData("storeId", storeId).putData("contact", contact).putData("tel", tel)
                        .putData("contactMail", contactMail).putData("lng", lng).putData("lat", lat).putData("createTime", CommonUtils.getCurDate()).putData("region", region);

                logoUrl = pd.getString("img0");
                ValidatorBreakException.NOT_TRUE(!(logoUrl == null || logoUrl.trim().length() == 0), "请上传商家LOGO");
                //文件大小检查
                String msg = CheckFile("img0");
                operaFlag = msg == "" || "".equals(msg) ? true : false;
                ValidatorBreakException.NOT_TRUE(operaFlag, msg);

                uploadModule1(UploadPathEnum.SHOP, 0);
                logoUrl = pd.getString("img0");

                String imgBg = pd.getString("img1");
                ValidatorBreakException.NOT_TRUE(!(imgBg == null || imgBg.trim().length() == 0), "请上传商家背景图");
                //文件大小检查
                String msg1 = CheckFile("img1");
                operaFlag = msg1 == "" || "".equals(msg1) ? true : false;
                ValidatorBreakException.NOT_TRUE(operaFlag, msg1);

                uploadModule1(UploadPathEnum.SHOPMAINPICURL, 1);
                String mainPicUrl = pd.getString("img1");

                String imgEw = pd.getString("img2");
                //		ValidatorBreakException.NOT_TRUE(!(imgEw == null || imgEw.trim().length() == 0), "请上传商家二维码");
                //文件大小检查
                String msg3 = CheckFile("img2");
                operaFlag = msg3 == "" || "".equals(msg3) ? true : false;
                ValidatorBreakException.NOT_TRUE(operaFlag, msg3);

                uploadModule1(UploadPathEnum.SHOPQRCODEURL, 2);
                String qrCodeUrl = pd.getString("img2");
                cd.put("logoUrl", logoUrl);
                //cd.put("mainPicUrl", mainPicUrl);
                cd.put("qrCodeUrl", qrCodeUrl);
                operaFlag = asShopMapper.isAccountExists(Conditions.newInstance().putData("name", name)) > 0;
                ValidatorBreakException.NOT_TRUE(!operaFlag, "商户名称已被注册！");

                operaFlag = asShopMapper.insertSelective(cd) == 1;
                ValidatorBreakException.NOT_TRUE(operaFlag);

                //插入商户背景图表数据
                AsShopPic pic = new AsShopPic();
                pic.setShopId(cd.getInt("shopId"));
                pic.setPicUrl(mainPicUrl);
                operaFlag = shopPicMapper.insertSelective(pic) == 1;
                ValidatorBreakException.NOT_TRUE(operaFlag);

                if (!StringUtils.isEmpty(cd.getStr("name"))) {
                    String pinyin = PinyinUtil.converterToFirstSpell(cd.getStr("name"));
                    if (pinyin.indexOf(",") > -1) {
                        String[] str = pinyin.split(",");
                        for (int j = 0; j < str.length; j++) {
                            AsShopSpell spell = new AsShopSpell();
                            spell.setShopId(cd.getInt("shopId"));
                            spell.setSpellValue(str[j]);
                            asShopSpellMapper.insertSelective(spell);
                        }
                    } else {
                        AsShopSpell spell = new AsShopSpell();
                        spell.setShopId(cd.getInt("shopId"));
                        spell.setSpellValue(pinyin);
                        asShopSpellMapper.insertSelective(spell);
                    }
                }
                // 商户账号建立　＆　账号名校验
                if (account.trim().equals("admin")) {
                    ValidatorBreakException.NOT_TRUE(!operaFlag, "账号已被注册");
                }
                // 校验帐号是否存在
                operaFlag = asUserMapper.isAccountExists(Conditions.newInstance().putData("account", account).putData("type", UserTypeEnum.STAFF.getId())) > 0;
                ValidatorBreakException.NOT_TRUE(!operaFlag, "账号已被注册");
                // 校验帐号是否存在
                operaFlag = asUserMapper.isAccountExists(Conditions.newInstance().putData("account", account).putData("type", UserTypeEnum.SELLER.getId())) > 0;
                ValidatorBreakException.NOT_TRUE(!operaFlag, "账号已被注册");

                // 添加后台用户信息
                String sys_user_id = get32UUID();
                Conditions sysUser = Conditions.newInstance();
                sysUser.putData("username", account).putData("name", account).putData("userId", sys_user_id).putData("createBy", getUser().getUSER_ID())
                        .putData("password", CommonUtils.encryptPwd(password, 2)).putData("asShopId", cd.getInt("shopId")).putData("status", 0)
                        .putData("skin", skin).putData("roleId", asDictParamMapper.findByKeyCode(DictParam.PARAM_SHOPOWNER_ROLE_ID));
                operaFlag = sysUserMapper.insertSelective(sysUser) == 1;
                ValidatorBreakException.NOT_TRUE(operaFlag);

                // 添加商家as_user记录
                Conditions user = Conditions.newInstance();
                user.putData("account", account).putData("password", CommonUtils.encryptPwd(password)).putData("createTime", CommonUtils.getCurDate())
                        .putData("type", UserTypeEnum.SELLER.getId()).putData("sysUserId", sys_user_id);
                operaFlag = asUserMapper.insertSelective(user) == 1;
                ValidatorBreakException.NOT_TRUE(operaFlag);

                // 添加商户管理员信息
                Conditions shopOwner = Conditions.newInstance();
                shopOwner.putData("userId", user.getInt("userId")).putData("shopId", cd.getInt("shopId")).putData("name", name).putData("tel", tel)
                        .putData("email", contactMail);
                operaFlag = asShopOwnerInfoMapper.insertSelective(shopOwner) == 1;
                ValidatorBreakException.NOT_TRUE(operaFlag);

                // 添加用户钱包
                AsWallet wallet = new AsWallet();
                wallet.setUserId(user.getInt("userId"));
                wallet.setCreateTime(CommonUtils.getCurDate());
                operaFlag = asWalletMapper.insertSelective(wallet) == 1;
                ValidatorBreakException.NOT_TRUE(operaFlag);

                // 添加员工帐号信息
                Conditions staffUser = Conditions.newInstance();
                staffUser.putData("account", account).putData("password", CommonUtils.encryptPwd(password)).putData("createTime", CommonUtils.getCurDate())
                        .putData("type", UserTypeEnum.STAFF.getId());
                operaFlag = asUserMapper.insertSelective(staffUser) == 1;
                ValidatorBreakException.NOT_TRUE(operaFlag);

                // 添加员工信息
                AsShopStaffInfo staffInfo = new AsShopStaffInfo();
                staffInfo.setUserId(staffUser.getInt("userId"));
                staffInfo.setShopUserId(shopOwner.getInt("userId"));
                staffInfo.setShopId(cd.getInt("shopId"));
                staffInfo.setDescp("");
                staffInfo.setName(name);
                staffInfo.setSn(get32UUID());
                staffInfo.setTel(tel);
                staffInfo.setEmail(contactMail);
                operaFlag = asShopStaffInfoMapper.insertSelective(staffInfo) == 1;
                ValidatorBreakException.NOT_TRUE(operaFlag);

                // 添加用户钱包
                AsWallet staffWallet = new AsWallet();
                staffWallet.setUserId(staffUser.getInt("userId"));
                staffWallet.setCreateTime(CommonUtils.getCurDate());
                operaFlag = asWalletMapper.insertSelective(staffWallet) == 1;
                ValidatorBreakException.NOT_TRUE(operaFlag);

                break;

            case EDIT:
                provinceId = 0;
                cityId = 0;
                areaId = 0;
                businessDistrictId = 0;
                businessDistrict = "";
                address = "";
                storeId = 0;
                businessDistrict = pd.getString("businessDistrictId");
                String districtId = businessDistrict == null || "".equals(businessDistrict) ? "0" : businessDistrict;
                businessDistrictId = Integer.parseInt(districtId);
                type = pd.getInt("shopType");
                lng = pd.getStringRequired("lng");
                lat = pd.getStringRequired("lat");
                region="";
                AsShop shop = asShopMapper.findByShopId(pd.getString("shopId"));
                AsShopOwnerInfo shopOwnerInfo = asShopOwnerInfoMapper.getOwnerInfoByShopId(shop.getShopId());
                //msg = CheckFile("img0");暂不验证图片尺寸
                //operaFlag = msg == "" || "".equals(msg) ? true : false;
                //ValidatorBreakException.NOT_TRUE(operaFlag, msg);

                uploadModule1(UploadPathEnum.SHOPMAINPICURL, 0);
                logoUrl = (pd.get("img0") == null || "".equals(pd.getString("img0"))) ? shop.getLogoUrl() : pd.get("img0").toString();

                //msg3 = CheckFile("img2");暂不验证图片尺寸
                //operaFlag = msg3 == "" || "".equals(msg3) ? true : false;
               //ValidatorBreakException.NOT_TRUE(operaFlag, msg3);

                uploadModule1(UploadPathEnum.SHOPQRCODEURL, 2);
                qrCodeUrl = (pd.get("img2") == null || "".equals(pd.getString("img2"))) ? shop.getQrCodeUrl() : pd.get("img2").toString();

                //判断商户背景图片
                List<AsShopPic> shopPics = shopPicMapper.selectByShopId(shop.getShopId());
                //图一
                uploadModule1(UploadPathEnum.SHOPMAINPICURL, 3);
                //图二
                uploadModule1(UploadPathEnum.SHOPMAINPICURL, 4);
                //图三
                uploadModule1(UploadPathEnum.SHOPMAINPICURL, 5);
                if(!shopPics.isEmpty()&&shopPics.size()>0){
                    //图一
                    String picUrl3 = (pd.get("img3") == null || "".equals(pd.getString("img3"))) ? shopPics.get(0).getPicUrl() : pd.get("img3").toString();
                    AsShopPic shopPic3 = shopPics.get(0);
                    shopPic3.setPicUrl(picUrl3);
                    shopPicMapper.updateByPrimaryKeySelective(shopPic3);
                    if(shopPics.size()>1){
                        //图二
                        String picUrl4 = (pd.get("img4") == null || "".equals(pd.getString("img4"))) ? shopPics.get(1).getPicUrl() : pd.get("img4").toString();
                        AsShopPic shopPic4 = shopPics.get(1);
                        shopPic4.setPicUrl(picUrl4);
                        shopPicMapper.updateByPrimaryKeySelective(shopPic4);
                    }
                    //当只有一张图片时
                    if(shopPics.size()==1){
                        if(StringUtils.isNotBlank(pd.getString("img4"))){
                            AsShopPic shopPic4 = new AsShopPic();
                            shopPic4.setPicUrl(pd.getString("img4"));
                            shopPic4.setShopId(shop.getShopId());
                            shopPicMapper.insertSelective(shopPic4);
                        }
                        if(StringUtils.isNotBlank(pd.getString("img5"))){
                            AsShopPic shopPic5 = new AsShopPic();
                            shopPic5.setPicUrl(pd.getString("img5"));
                            shopPic5.setShopId(shop.getShopId());
                            shopPicMapper.insertSelective(shopPic5);
                        }
                    }
                    //当只有两张图片时
                    if(shopPics.size()==2){
                        if(StringUtils.isNotBlank(pd.getString("img5"))){
                            AsShopPic shopPic5 = new AsShopPic();
                            shopPic5.setPicUrl(pd.getString("img5"));
                            shopPic5.setShopId(shop.getShopId());
                            shopPicMapper.insertSelective(shopPic5);
                        }
                    }
                    if(shopPics.size()>2){
                        //图三
                        String picUrl5 = (pd.get("img5") == null || "".equals(pd.getString("img5"))) ? shopPics.get(2).getPicUrl() : pd.get("img5").toString();
                        AsShopPic shopPic5 = shopPics.get(2);
                        shopPic5.setPicUrl(picUrl5);
                        shopPicMapper.updateByPrimaryKeySelective(shopPic5);
                    }
                }else{
                    //当一张背景图都没有的时候
                    if(StringUtils.isNotBlank(pd.getString("img3"))){
                        AsShopPic shopPic3 = new AsShopPic();
                        shopPic3.setPicUrl(pd.getString("img3"));
                        shopPic3.setShopId(shop.getShopId());
                        shopPicMapper.insertSelective(shopPic3);
                    }
                    if(StringUtils.isNotBlank(pd.getString("img4"))){
                        AsShopPic shopPic4 = new AsShopPic();
                        shopPic4.setPicUrl(pd.getString("img4"));
                        shopPic4.setShopId(shop.getShopId());
                        shopPicMapper.insertSelective(shopPic4);
                    }
                    if(StringUtils.isNotBlank(pd.getString("img5"))){
                        AsShopPic shopPic5 = new AsShopPic();
                        shopPic5.setPicUrl(pd.getString("img5"));
                        shopPic5.setShopId(shop.getShopId());
                        shopPicMapper.insertSelective(shopPic5);
                    }
                }

                vshopName = (String) pd.get("vshopName");
                shopName = pd.getStringRequired("shopName");
                name = "";

                floor = 0;
                tradeCategoryId = Integer.parseInt(pd.getString("tradeCategoryId"));
                if (vshopName != null && !("".equals(vshopName)) && type == 2) {
                    name = vshopName;
                    floor = pd.getInt("floor");
                    //String vid = "".equals(pd.get("storeId").toString()) ? "0" : pd.get("storeId").toString();
                    //storeId = Integer.parseInt(vid);
                    storeId = pd.getInt("storeId");
                    provinceId = pd.getInt("provinceId");
                    cityId = pd.getInt("cityId");
                    areaId = pd.getInt("areaId");
                    businessDistrict = pd.getString("businessDistrictId");
                    String istrictId = businessDistrict == null || "".equals(businessDistrict) ? "0" : businessDistrict;
                    businessDistrictId = Integer.parseInt(istrictId);
                    address = pd.getStringRequired("address");
                    region = pd.getStringRequired("region");

                }
                if (shopName != null && !("".equals(shopName)) && type == 1) {
                    name = shopName.substring(0, shopName.indexOf("_"));
                    storeId = Integer.parseInt(shopName.substring(shopName.indexOf("_") + 1, shopName.length()));
                    Map ojb = (Map) asStoreMapperr.getAddress(storeId);

                    provinceId = CommonUtils.o2i(ojb.get("provinceId"));
                    cityId = CommonUtils.o2i(ojb.get("cityId"));
                    areaId = CommonUtils.o2i(ojb.get("areaId"));
                    lng = String.valueOf(ojb.get("lng"));
                    lat = String.valueOf(ojb.get("lat"));
                    address = String.valueOf(ojb.get("address"));
                    businessDistrictId = CommonUtils.o2i(ojb.get("businessDistrictId"));
                }
                int shopId = pd.getInt("shopId");

                contact = pd.getStringRequired("contact");
                tel = pd.getStringRequired("tel");
                contactMail = pd.getStringRequired("contactMail");
                Conditions cdShop = Conditions.newInstance().putData("businessDistrictId", businessDistrictId).putData("name", name).putData("type", type)
                        .putData("logoUrl", logoUrl).putData("qrCodeUrl", qrCodeUrl).putData("provinceId", provinceId)
                        .putData("cityId", cityId).putData("areaId", areaId).putData("address", address).putData("contact", contact).putData("tel", tel)
                        .putData("shopId", shopId).putData("tradeCategoryId", tradeCategoryId).putData("contactMail", contactMail).putData("storeId", storeId)
                        .putData("floor", floor).putData("lng", lng).putData("lat", lat).putData("createTime", CommonUtils.getCurDate()).putData("region", region);
                operaFlag = asShopMapper.updateByPrimaryKeySelective(cdShop) == 1;
                ValidatorBreakException.NOT_TRUE(operaFlag);
                if (!StringUtils.isEmpty(cdShop.getStr("name"))) {
                    String pinyin = PinyinUtil.converterToFirstSpell(cdShop.getStr("name"));
                    List<AsShopSpell> asShopSpell = asShopSpellMapper.selectByShopId(cdShop.getInt("shopId"));
                    if(asShopSpell==null||asShopSpell.size() ==0){
                        if (pinyin.indexOf(",") > -1 ) {
                            String[] str = pinyin.split(",");
                            for (int j = 0; j < str.length; j++) {
                                AsShopSpell spell = new AsShopSpell();
                                spell.setShopId(cdShop.getInt("shopId"));
                                spell.setSpellValue(str[j]);
                                asShopSpellMapper.insertSelective(spell);
                            }
                        } if(!(pinyin.indexOf(",") > -1) && asShopSpell.size()==0) {
                            AsShopSpell spell = new AsShopSpell();
                            spell.setShopId(cdShop.getInt("shopId"));
                            spell.setSpellValue(pinyin);
                            asShopSpellMapper.insertSelective(spell);
                        }
                    }else{
                        asShopSpellMapper.deleteByPrimaryKeyByshopId(cdShop.getInt("shopId"));
                        if (pinyin.indexOf(",") > -1 ) {
                            String[] str = pinyin.split(",");
                            for (int j = 0; j < str.length; j++) {
                                AsShopSpell spell = new AsShopSpell();
                                spell.setShopId(cdShop.getInt("shopId"));
                                spell.setSpellValue(str[j]);
                                asShopSpellMapper.insertSelective(spell);
                            }
                        } if(!(pinyin.indexOf(",") > -1) && asShopSpell.size()>0) {
                            AsShopSpell spell = new AsShopSpell();
                            spell.setShopId(cdShop.getInt("shopId"));
                            spell.setSpellValue(pinyin);
                            asShopSpellMapper.insertSelective(spell);
                        }
                    }
                }
                shopOwnerInfo.setName(cdShop.getStr("name"));
                shopOwnerInfo.setTel(cdShop.getStr("tel"));
                shopOwnerInfo.setEmail(contactMail);
                //shopOwnerInfo.setAuditStatus(0);
                operaFlag = asShopOwnerInfoMapper.updateByPrimaryKeySelective(shopOwnerInfo) == 1;
                ValidatorBreakException.NOT_TRUE(operaFlag);
                break;

            default:
                ValidatorBreakException.NOT_TRUE(false);
        }
        addViewData("action", action.getId());
        return shopInfoPage(c);
    }

    /**
     * 操作编辑当前登录商家个人信息
     *
     * @return c
     * @author tgb
     * @date 2017年02月21日
     */
    public Object operaOwner(BaseController c) {

        initialized(c);

        OperaEnum action = OperaEnum.get(pd.getInt("action"));

        switch (action) {

            case EDIT:
                Integer shopOwnerInfoId = pd.getParaForInteger("shopOwnerInfoId");
                AsShopOwnerInfo shopOwnerInfo = asShopOwnerInfoMapper.findByShopOwnerId(shopOwnerInfoId);
                addViewData("shopId", shopOwnerInfo.getShopId());
                ValidatorBreakException.NOT_NULL(shopOwnerInfo, 1001, "系统繁忙", 1);
                Integer applicantCertificateType = pd.getParaForInteger("applicantCertificateType");
                Integer corporationCertificateType = pd.getParaForInteger("corporationCertificateType");
                String applicant = pd.getParaForString("applicant");
                String applicantCertificateNum = pd.getParaForString("applicantCertificateNum");
                String corporation = pd.getParaForString("corporation");
                String corporationCertificateNum = pd.getParaForString("corporationCertificateNum");

                //身份证正面
                uploadModule1(UploadPathEnum.USERS, 1);
                String identityFront = StringUtils.isNotBlank(pd.getString("img1")) ? pd.getString("img1")
                        .toString() : shopOwnerInfo.getIdentityFront();
                //身份证背面
                uploadModule1(UploadPathEnum.USERS, 2);
                String identityBack = StringUtils.isNotBlank(pd.getString("img2")) ? pd.getString("img2")
                        .toString() : shopOwnerInfo.getIdentityBack();
                //营业执照
                uploadModule1(UploadPathEnum.USERS, 3);
                String businessLicence = StringUtils.isNotBlank(pd.getString("img3")) ? pd.getString("img3")
                        .toString() : shopOwnerInfo.getBusinessLicence();
                //经营许可证
                uploadModule1(UploadPathEnum.USERS, 4);
                String businessCertificate = StringUtils.isNotBlank(pd.getString("img4")) ? pd.getString("img4")
                        .toString() : shopOwnerInfo.getBusinessCertificate();
                //更新商户个人信息
                Conditions shopOwner = Conditions.newInstance();
                shopOwner.putData("shopOwnerInfoId", shopOwnerInfoId)
                        .putData("applicant", applicant)
                        .putData("applicantCertificateType", applicantCertificateType)
                        .putData("applicantCertificateNum", applicantCertificateNum)
                        .putData("corporation", corporation)
                        .putData("corporationCertificateType", corporationCertificateType)
                        .putData("corporationCertificateNum", corporationCertificateNum)
                        .putData("identityFront", identityFront)
                        .putData("identityBack", identityBack)
                        .putData("businessLicence", businessLicence)
                        .putData("businessCertificate", businessCertificate);
                operaFlag = asShopOwnerInfoMapper.updateByPrimaryKeySelective(shopOwner) == 1;
                ValidatorBreakException.NOT_TRUE(operaFlag);
                break;
            default:
                ValidatorBreakException.NOT_TRUE(false);
        }
        addViewData("action", action.getId());
        return ownerInfoPage(c);
    }

    /**
     * 操作
     *
     * @return c
     * @author huangxw
     * @date 2017年02月21日
     */
    public Object opera(BaseController c) {

        initialized(c);

        OperaEnum action = OperaEnum.get(pd.getInt("action"));

        switch (action) {
            case ADD:
                String vshopName = (String) pd.get("vshopName");
                String shopName = pd.getStringRequired("shopName");
                String name = "";
                Integer floor = 0;
                Integer tradeCategoryId = 0;
                int provinceId = 0;
                int cityId = 0;
                int areaId = 0;
                int businessDistrictId = 0;
                String businessDistrict = "";
                String address = "";
                Integer storeId = 0;
                int type = pd.getInt("shopType");
                String lng = pd.getStringRequired("lng");
                String lat = pd.getStringRequired("lat");
                String skin = "";
                String region = null;
                if (vshopName != null && !("".equals(vshopName)) && type == 2) {
                    name = vshopName;
                    tradeCategoryId = Integer.parseInt(pd.getString("tradeCategoryId"));
                    floor = pd.getInt("floor");
                    provinceId = pd.getInt("provinceId");
                    cityId = pd.getInt("cityId");
                    areaId = pd.getInt("areaId");
                    String vid = "".equals(pd.get("storeId").toString()) ? "0" : pd.get("storeId").toString();
                    storeId = Integer.parseInt(vid);
                    businessDistrict = pd.getString("businessDistrictId");
                    String DistrictId = businessDistrict == null || "".equals(businessDistrict) ? "0" : businessDistrict;
                    businessDistrictId = Integer.parseInt(DistrictId);
                    address = pd.getStringRequired("address");
                    region = pd.getStringRequired("region");
                    skin = "skin-2";
                }
                if (shopName != null && !("".equals(shopName)) && type == 1) {
                    name = shopName.substring(0, shopName.indexOf("_"));
                    storeId = Integer.parseInt(shopName.substring(shopName.indexOf("_") + 1, shopName.length()));
                    Map<String, String> ojb = (Map) asStoreMapperr.getAddress(storeId);
                    provinceId = CommonUtils.o2i(ojb.get("provinceId"));
                    cityId = CommonUtils.o2i(ojb.get("cityId"));
                    areaId = CommonUtils.o2i(ojb.get("areaId"));
                    lng = String.valueOf(ojb.get("lng"));
                    lat = String.valueOf(ojb.get("lat"));
                    address = String.valueOf(ojb.get("address"));
                    businessDistrictId = CommonUtils.o2i(ojb.get("businessDistrictId"));
                    skin = "skin-2";
                }

                String contact = pd.getStringRequired("contact");
                String tel = pd.getStringRequired("tel");
                String contactMail = pd.getStringRequired("contactMail");
                String logoUrl = pd.get("logoUrl") == null ? "" : pd.get("logoUrl").toString();

                String account = pd.getStringRequired("account");
                String password = pd.getStringRequired("password");

                Conditions cd = Conditions.newInstance().putData("type", type).putData("name", name).putData("provinceId", provinceId).putData("cityId", cityId)
                        .putData("areaId", areaId).putData("businessDistrictId", businessDistrictId).putData("tradeCategoryId", tradeCategoryId)
                        .putData("floor", floor).putData("address", address).putData("storeId", storeId).putData("contact", contact).putData("tel", tel)
                        .putData("contactMail", contactMail).putData("lng", lng).putData("lat", lat).putData("createTime", CommonUtils.getCurDate()).putData("region", region);

                logoUrl = pd.getString("img0");
                ValidatorBreakException.NOT_TRUE(!(logoUrl == null || logoUrl.trim().length() == 0), "请上传商家LOGO");
                //文件大小检查
                String msg = CheckFile("img0");
                operaFlag = msg == "" || "".equals(msg) ? true : false;
                ValidatorBreakException.NOT_TRUE(operaFlag, msg);

                uploadModule1(UploadPathEnum.SHOP, 0);
                logoUrl = pd.getString("img0");

                String imgBg = pd.getString("img1");
                ValidatorBreakException.NOT_TRUE(!(imgBg == null || imgBg.trim().length() == 0), "请上传商家背景图");
                //文件大小检查
                String msg1 = CheckFile("img1");
                operaFlag = msg1 == "" || "".equals(msg1) ? true : false;
                ValidatorBreakException.NOT_TRUE(operaFlag, msg1);

                uploadModule1(UploadPathEnum.SHOPMAINPICURL, 1);
                String mainPicUrl = pd.getString("img1");

                String imgEw = pd.getString("img2");
                //		ValidatorBreakException.NOT_TRUE(!(imgEw == null || imgEw.trim().length() == 0), "请上传商家二维码");
                //文件大小检查
                String msg3 = CheckFile("img2");
                operaFlag = msg3 == "" || "".equals(msg3) ? true : false;
                ValidatorBreakException.NOT_TRUE(operaFlag, msg3);

                uploadModule1(UploadPathEnum.SHOPQRCODEURL, 2);
                String qrCodeUrl = pd.getString("img2");
                cd.put("logoUrl", logoUrl);
                //cd.put("mainPicUrl", mainPicUrl);
                cd.put("qrCodeUrl", qrCodeUrl);
                operaFlag = asShopMapper.isAccountExists(Conditions.newInstance().putData("name", name)) > 0;
                ValidatorBreakException.NOT_TRUE(!operaFlag, "商户名称已被注册！");

                operaFlag = asShopMapper.insertSelective(cd) == 1;
                ValidatorBreakException.NOT_TRUE(operaFlag);

                //插入商户背景图表数据
                AsShopPic pic = new AsShopPic();
                pic.setShopId(cd.getInt("shopId"));
                pic.setPicUrl(mainPicUrl);
                operaFlag = shopPicMapper.insertSelective(pic) == 1;
                ValidatorBreakException.NOT_TRUE(operaFlag);

                if (!StringUtils.isEmpty(cd.getStr("name"))) {
                    String pinyin = PinyinUtil.converterToFirstSpell(cd.getStr("name"));
                    if (pinyin.indexOf(",") > -1) {
                        String[] str = pinyin.split(",");
                        for (int j = 0; j < str.length; j++) {
                            AsShopSpell spell = new AsShopSpell();
                            spell.setShopId(cd.getInt("shopId"));
                            spell.setSpellValue(str[j]);
                            asShopSpellMapper.insertSelective(spell);
                        }
                    } else {
                        AsShopSpell spell = new AsShopSpell();
                        spell.setShopId(cd.getInt("shopId"));
                        spell.setSpellValue(pinyin);
                        asShopSpellMapper.insertSelective(spell);
                    }
                }
                // 商户账号建立　＆　账号名校验
                if (account.trim().equals("admin")) {
                    ValidatorBreakException.NOT_TRUE(!operaFlag, "账号已被注册");
                }
                // 校验帐号是否存在
                operaFlag = asUserMapper.isAccountExists(Conditions.newInstance().putData("account", account).putData("type", UserTypeEnum.STAFF.getId())) > 0;
                ValidatorBreakException.NOT_TRUE(!operaFlag, "账号已被注册");
             // 校验帐号是否存在
                operaFlag = asUserMapper.isAccountExists(Conditions.newInstance().putData("account", account).putData("type", UserTypeEnum.SELLER.getId())) > 0;
                ValidatorBreakException.NOT_TRUE(!operaFlag, "账号已被注册");

                // 添加后台用户信息
                String sys_user_id = get32UUID();
                Conditions sysUser = Conditions.newInstance();
                sysUser.putData("username", account).putData("name", account).putData("userId", sys_user_id).putData("createBy", getUser().getUSER_ID())
                        .putData("password", CommonUtils.encryptPwd(password, 2)).putData("asShopId", cd.getInt("shopId")).putData("status", 0)
                        .putData("skin", skin).putData("roleId", asDictParamMapper.findByKeyCode(DictParam.PARAM_SHOPOWNER_ROLE_ID));
                operaFlag = sysUserMapper.insertSelective(sysUser) == 1;
                ValidatorBreakException.NOT_TRUE(operaFlag);

                // 添加商家as_user记录
                Conditions user = Conditions.newInstance();
                user.putData("account", account).putData("password", CommonUtils.encryptPwd(password)).putData("createTime", CommonUtils.getCurDate())
                        .putData("type", UserTypeEnum.SELLER.getId()).putData("sysUserId", sys_user_id);
                operaFlag = asUserMapper.insertSelective(user) == 1;
                ValidatorBreakException.NOT_TRUE(operaFlag);

                // 添加商户管理员信息
                Conditions shopOwner = Conditions.newInstance();
                shopOwner.putData("userId", user.getInt("userId")).putData("shopId", cd.getInt("shopId")).putData("name", name).putData("tel", tel)
                        .putData("email", contactMail);
                operaFlag = asShopOwnerInfoMapper.insertSelective(shopOwner) == 1;
                ValidatorBreakException.NOT_TRUE(operaFlag);

                // 添加用户钱包
                AsWallet wallet = new AsWallet();
                wallet.setUserId(user.getInt("userId"));
                wallet.setCreateTime(CommonUtils.getCurDate());
                operaFlag = asWalletMapper.insertSelective(wallet) == 1;
                ValidatorBreakException.NOT_TRUE(operaFlag);

                // 添加员工帐号信息
                Conditions staffUser = Conditions.newInstance();
                staffUser.putData("account", account).putData("password", CommonUtils.encryptPwd(password)).putData("createTime", CommonUtils.getCurDate())
                        .putData("type", UserTypeEnum.STAFF.getId());
                operaFlag = asUserMapper.insertSelective(staffUser) == 1;
                ValidatorBreakException.NOT_TRUE(operaFlag);

                // 添加员工信息
                AsShopStaffInfo staffInfo = new AsShopStaffInfo();
                staffInfo.setUserId(staffUser.getInt("userId"));
                staffInfo.setShopUserId(shopOwner.getInt("userId"));
                staffInfo.setShopId(cd.getInt("shopId"));
                staffInfo.setDescp("");
                staffInfo.setName(name);
                staffInfo.setSn(get32UUID());
                staffInfo.setTel(tel);
                staffInfo.setEmail(contactMail);
                operaFlag = asShopStaffInfoMapper.insertSelective(staffInfo) == 1;
                ValidatorBreakException.NOT_TRUE(operaFlag);

                // 添加用户钱包
                AsWallet staffWallet = new AsWallet();
                staffWallet.setUserId(staffUser.getInt("userId"));
                staffWallet.setCreateTime(CommonUtils.getCurDate());
                operaFlag = asWalletMapper.insertSelective(staffWallet) == 1;
                ValidatorBreakException.NOT_TRUE(operaFlag);

                break;

            case EDIT:
                provinceId = 0;
                cityId = 0;
                areaId = 0;
                businessDistrictId = 0;
                businessDistrict = "";
                address = "";
                storeId = 0;
                businessDistrict = pd.getString("businessDistrictId");
                String districtId = businessDistrict == null || "".equals(businessDistrict) ? "0" : businessDistrict;
                businessDistrictId = Integer.parseInt(districtId);
                type = pd.getInt("shopType");
                lng = pd.getStringRequired("lng");
                lat = pd.getStringRequired("lat");
                region="";
                AsShop shop = asShopMapper.findByShopId(pd.getString("shopId"));
                msg = CheckFile("img0");
                operaFlag = msg == "" || "".equals(msg) ? true : false;
                ValidatorBreakException.NOT_TRUE(operaFlag, msg);

                uploadModule1(UploadPathEnum.SHOPMAINPICURL, 0);
                logoUrl = (pd.get("img0") == null || "".equals(pd.getString("img0"))) ? shop.getLogoUrl() : pd.get("img0").toString();

			/*msg1 = CheckFile("img1");
            operaFlag = msg1 == "" || "".equals(msg1) ? true : false;
			ValidatorBreakException.NOT_TRUE(operaFlag, msg1);
			uploadModule1(UploadPathEnum.SHOP, 1);
			mainPicUrl = (pd.get("img1") == null || "".equals(pd.getString("img1"))) ? shop.getMainPicUrl() : pd.get("img1").toString();*/
                msg3 = CheckFile("img2");
                operaFlag = msg3 == "" || "".equals(msg3) ? true : false;
                ValidatorBreakException.NOT_TRUE(operaFlag, msg3);

                uploadModule1(UploadPathEnum.SHOPQRCODEURL, 2);
                qrCodeUrl = (pd.get("img2") == null || "".equals(pd.getString("img2"))) ? shop.getQrCodeUrl() : pd.get("img2").toString();
                vshopName = (String) pd.get("vshopName");
                shopName = pd.getStringRequired("shopName");
                name = "";

                floor = 0;
                tradeCategoryId = Integer.parseInt(pd.getString("tradeCategoryId"));
                if (vshopName != null && !("".equals(vshopName)) && type == 2) {
                    name = vshopName;
                    floor = pd.getInt("floor");
                    String vid = "".equals(pd.get("storeId").toString()) ? "0" : pd.get("storeId").toString();
                    storeId = Integer.parseInt(vid);
                    provinceId = pd.getInt("provinceId");
                    cityId = pd.getInt("cityId");
                    areaId = pd.getInt("areaId");
                    businessDistrict = pd.getString("businessDistrictId");
                    String istrictId = businessDistrict == null || "".equals(businessDistrict) ? "0" : businessDistrict;
                    businessDistrictId = Integer.parseInt(istrictId);
                    address = pd.getStringRequired("address");
                    region = pd.getStringRequired("region");

                }
                if (shopName != null && !("".equals(shopName)) && type == 1) {
                    name = shopName.substring(0, shopName.indexOf("_"));
                    storeId = Integer.parseInt(shopName.substring(shopName.indexOf("_") + 1, shopName.length()));
                    Map ojb = (Map) asStoreMapperr.getAddress(storeId);

                    provinceId = CommonUtils.o2i(ojb.get("provinceId"));
                    cityId = CommonUtils.o2i(ojb.get("cityId"));
                    areaId = CommonUtils.o2i(ojb.get("areaId"));
                    lng = String.valueOf(ojb.get("lng"));
                    lat = String.valueOf(ojb.get("lat"));
                    address = String.valueOf(ojb.get("address"));
                    businessDistrictId = CommonUtils.o2i(ojb.get("businessDistrictId"));
                }
                int shopId = pd.getInt("shopId");

                contact = pd.getStringRequired("contact");
                tel = pd.getStringRequired("tel");
                contactMail = pd.getStringRequired("contactMail");
                Conditions cdShop = Conditions.newInstance().putData("businessDistrictId", businessDistrictId).putData("name", name).putData("type", type)
                        .putData("logoUrl", logoUrl).putData("qrCodeUrl", qrCodeUrl).putData("provinceId", provinceId)
                        .putData("cityId", cityId).putData("areaId", areaId).putData("address", address).putData("contact", contact).putData("tel", tel)
                        .putData("shopId", shopId).putData("tradeCategoryId", tradeCategoryId).putData("contactMail", contactMail).putData("storeId", storeId)
                        .putData("floor", floor).putData("lng", lng).putData("lat", lat).putData("createTime", CommonUtils.getCurDate()).putData("region", region);
                operaFlag = asShopMapper.updateByPrimaryKeySelective(cdShop) == 1;
                ValidatorBreakException.NOT_TRUE(operaFlag);
                if (!StringUtils.isEmpty(cdShop.getStr("name"))) {
                    String pinyin = PinyinUtil.converterToFirstSpell(cdShop.getStr("name"));
                    List<AsShopSpell> asShopSpell = asShopSpellMapper.selectByShopId(cdShop.getInt("shopId"));
                    if(asShopSpell==null||asShopSpell.size() ==0){
                        if (pinyin.indexOf(",") > -1 ) {
                            String[] str = pinyin.split(",");
                            for (int j = 0; j < str.length; j++) {
                                AsShopSpell spell = new AsShopSpell();
                                spell.setShopId(cdShop.getInt("shopId"));
                                spell.setSpellValue(str[j]);
                                asShopSpellMapper.insertSelective(spell);
                            }
                        } if(!(pinyin.indexOf(",") > -1) && asShopSpell.size()==0) {
                            AsShopSpell spell = new AsShopSpell();
                            spell.setShopId(cdShop.getInt("shopId"));
                            spell.setSpellValue(pinyin);
                            asShopSpellMapper.insertSelective(spell);
                        }
                    }else{
                        asShopSpellMapper.deleteByPrimaryKeyByshopId(cdShop.getInt("shopId"));
                        if (pinyin.indexOf(",") > -1 ) {
                            String[] str = pinyin.split(",");
                            for (int j = 0; j < str.length; j++) {
                                AsShopSpell spell = new AsShopSpell();
                                spell.setShopId(cdShop.getInt("shopId"));
                                spell.setSpellValue(str[j]);
                                asShopSpellMapper.insertSelective(spell);
                            }
                        } if(!(pinyin.indexOf(",") > -1) && asShopSpell.size()>0) {
                            AsShopSpell spell = new AsShopSpell();
                            spell.setShopId(cdShop.getInt("shopId"));
                            spell.setSpellValue(pinyin);
                            asShopSpellMapper.insertSelective(spell);
                        }
                    }

                }
                break;
            case DELETE:
                operaFlag = asShopMapper.isCouponExists(pd) > 0;
                ValidatorBreakException.NOT_TRUE(!operaFlag, "该商家有优惠券正参与活动不能删除！");

                operaFlag = asShopMapper.isPrizeExists(pd) > 0;
                ValidatorBreakException.NOT_TRUE(!operaFlag, "该商家有礼品券正参与活动不能删除！");

                operaFlag = sysUserMapper.deleteData(pd) == 1;//删除系统用户
                ValidatorBreakException.NOT_NULL(operaFlag, "数据信息已过期，请刷新后重试");
                //查询员工管理员账号
                List<String> staList = shopStaffInfoMapper.findData(pd);
                if (Tools.listIsEmpty(staList)) {
                    String[] array = new String[staList.size()];
                    staList.toArray(array);
                    operaFlag = asUserMapper.deleteAll(array) == 1;
                    ValidatorBreakException.NOT_NULL(operaFlag, "数据信息已过期，请刷新后重试");
                }
                List<String> shopList = asShopOwnerInfoMapper.findData(pd);
                if (Tools.listIsEmpty(staList)) {
                    //查询商场管理员账号
                    String[] arr = new String[shopList.size()];
                    shopList.toArray(arr);
                    asUserMapper.deleteAll(arr);
                }
                operaFlag = shopStaffInfoMapper.deleteAlls(pd) == 1;
                ValidatorBreakException.NOT_NULL(operaFlag, "数据信息已过期，请刷新后重试");

                operaFlag = asShopOwnerInfoMapper.deleteAlls(pd) == 1;
                ValidatorBreakException.NOT_NULL(operaFlag, "数据信息已过期，请刷新后重试");

                operaFlag = asShopMapper.deleteByIds(pd) == 1;
                ValidatorBreakException.NOT_NULL(operaFlag, "数据信息已过期，请刷新后重试");
                break;
            default:
                ValidatorBreakException.NOT_TRUE(false);
        }

        return renderSuccess();
    }

    /**
     * 查询商户管理列表-运营
     *
     * @param c
     * @return
     * @author tgb
     * @date 2016年10月28日
     */
    public Object getShopAccountList(BaseController c) {

        initialized(c);

        pageQueryModule(asUserMapper, "getUserWhitConditions", true);

        return renderView(PAGE_PATH + "shop_account_list");
    }

    /**
     * 店铺管理列表-业主
     *
     * @param c
     * @return
     * @author tgb
     * @date 2017年06月08日
     */
    public Object getShopAccountOwnerList(BaseController c) {

        initialized(c);
        //判断是否商场账号
        if(!isRoomAdmin()){
            AsShop asShop =  asShopMapper.findByShopId(getUser().getAs_shop_id());
            addField("storeId", asShop.getStoreId());
            addViewData("shopId",getUser().getAs_shop_id());
        }
        pageQueryModule(asUserMapper, "getShopAccountOwnerList", true);
        return renderView(PAGE_PATH + "shop_account_owner_list");
    }

    /**
     * 修改密码
     *
     * @author tgb
     * @date 2017年06月8日
     * @param c
     * @return
     */
    public Object updatePwd(BaseController c) {

        initialized(c);

        String password = pd.getParaForString("password");
        String confirmPassword = pd.getParaForString("confirmPassword");
        ValidatorBreakException.NOT_TRUE(password.equals(confirmPassword), "两次密码输入不一样");

        int userId = pd.getInt("userId");

        AsUser asUser = asUserMapper.getUserByUserId(userId);
        ValidatorBreakException.NOT_NULL(asUser, "系统繁忙");

        SysUser sysUser = (SysUser) sysUserMapper.selectByPrimaryKey(asUser.getSysUserId());
        ValidatorBreakException.NOT_NULL(sysUser, "系统繁忙");

        String SHA1Password = CommonUtils.encryptPwd(password, 2);
        String MD5Password = CommonUtils.encryptPwd(password);

        // 修改后台用户表密码
        sysUser.setPassword(SHA1Password);
        operaFlag = sysUserMapper.updateByPrimaryKeySelective(sysUser) == 1;
        ValidatorBreakException.NOT_TRUE(operaFlag, "系统繁忙");

        // 修改账号表密码
        asUser.setPassword(MD5Password);
        operaFlag = asUserMapper.updateByPrimaryKeySelective(asUser) == 1;
        ValidatorBreakException.NOT_TRUE(operaFlag, "系统繁忙");

        return renderSuccess();
    }

    /**
     * 获取普通用户列表
     *
     * @param c
     * @return
     * @author yangyanchao
     * @date 2016年12月26日
     */
    public Object getCommonUserList(BaseController c) {

        initialized(c);

        List<AsProvince> provinceList = provinceMapper.selectByAttr(pd);
        List<AsCity> cityList = asCityMapper.selectByAttr(null);
        List<AsArea> areaList = asAreaMapper.selectByAttr(null);

        addViewData("provinceList", provinceList);
        addViewData("cityList", cityList);
        addViewData("areaList", areaList);
        List<Object> list = pageQueryModule(asUserMapper, "getCommonUserList", true);
        for (int i = 0; i < list.size(); i++) {
            Map<String, Object> map = (Map<String, Object>) list.get(i);
            String inviteCode = map.get("inviteCode") == null ? null : map.get("inviteCode").toString();
            //生成邀请码
			/*if (map.get("myInvite") == null) {
				pd.put("userId", map.get("userId"));
				AsUserInfo record = userInfoMapper.getUserInfoList(pd);
				AsUserInfo newRecord = new AsUserInfo();
				newRecord.setUserInfoId(record.getUserInfoId());
				newRecord.setMyInvite(RandomCodeUtil.idToCode(Integer.valueOf(String.valueOf(map.get("userId"))), 6));
				userInfoMapper.updateByPrimaryKeySelective(newRecord);
			}*/
            if (inviteCode != null) {
                AsUserInfo record = new AsUserInfo();
                record.setInviteCode(inviteCode);
                List<AsUserInfo> recordList = userInfoMapper.queryUserInfo(record);
                if (recordList.size() > 0)
                    map.put("inviteRefId", recordList.get(0).getAccount());
            }
        }

        return renderView(PAGE_PATH + "common_account_list");
    }

    /**
     * 添加商家端帐号
     *
     * @param c
     * @return
     * @author yangyanchao
     * @date 2016年10月31日
     */
    public Object saveShopAccountPage(BaseController c) {

        initialized(c);

        OperaEnum action = OperaEnum.get(pd.getInt("action") == 0 ? 1 : pd.getInt("action"));
        switch (action) {

            case ADD:

                List<Object> shops = queryModule(asShopMapper, "listShopAll", false);

                addViewData("shops", shops);

                break;
            case EDIT:
                shops = queryModule(asShopMapper, "listShopAll", false);
                addViewData("shops", shops);
                if (UserTypeEnum.SELLER == UserTypeEnum.GET(pd.getInt("type"))) {
                    Conditions shopOwner = asShopOwnerInfoMapper.findOwnerInfo(pd.getInt("userId"));
                    addViewData("shop", shopOwner);
                    addViewData("pd", pd);
                }
                if (UserTypeEnum.STAFF == UserTypeEnum.GET(pd.getInt("type"))) {
                    Conditions staff = asShopStaffInfoMapper.findStaffInfo(pd.getInt("userId"));
                    addViewData("shop", staff);
                    addViewData("pd", pd);
                }
                List<AsYpScreenDomain> domainList = asYpScreenDomainMapper.getDomains(pd);// 站点
                JSONArray domains = JSONArray.fromObject(domainList);
                addViewData("domains", domains);
                break;
            case QUERY:
                shops = queryModule(asShopMapper, "listShopAll", false);
                addViewData("shops", shops);
                if (UserTypeEnum.SELLER == UserTypeEnum.GET(pd.getInt("type"))) {
                    Conditions shopOwner = asShopOwnerInfoMapper.findOwnerInfo(pd.getInt("userId"));
                    addViewData("shop", shopOwner);
                }
                if (UserTypeEnum.STAFF == UserTypeEnum.GET(pd.getInt("type"))) {
                    Conditions staff = asShopStaffInfoMapper.findStaffInfo(pd.getInt("userId"));
                    addViewData("shop", staff);

                }
                addViewData("pd", pd);
                shops = queryModule(asShopMapper, "listShopAll", false);
                domainList = asYpScreenDomainMapper.getDomains(pd);// 站点
                domains = JSONArray.fromObject(domainList);
                addViewData("domains", domains);
                break;
            default:
                ValidatorBreakException.NOT_NULL(null);
        }

        addField("action", action.getId());

        return renderView(PAGE_PATH + "save_shop_account");
    }

    /**
     * 异步检查后台员工账号是否重名
     *
     * @return
     * @author tgb 创建时间：2016年10月26日
     * @version 1.0
     */
    public Object isShopAccountExist(BaseController c) {
        initialized(c);
        String account = pd.getParaForString("account");
        int data = sysUserMapper.userNameIsExist(Conditions.newInstance().putData("account", account).putData("type", UserTypeEnum.STAFF.getId()));
        if (data > 0) {
            return true;
        }
        return false;
    }

    /**
     * 保存添加商家帐号表单数据
     *
     * @param c
     * @return
     * @author yangyanchao
     * @date 2016年11月17日
     */
    public Object saveShopAccount(BaseController c) {

        initialized(c);

        OperaEnum action = OperaEnum.get(pd.getInt("action") == 0 ? 1 : pd.getInt("action"));
        switch (action) {

            case ADD:

                int shopId = pd.getParaForPositiveInteger("shopId");
                int type = pd.getParaForPositiveInteger("type");
                int screenDomainId = 0;
                String name = pd.getParaForString("name");
                String email = pd.getParaForString("email");
                String tel = pd.getParaForString("tel");
                String account = pd.getParaForString("account");
                String password = pd.getParaForString("password");

                // 账号名校验
                if (account.trim().equals("admin")) {
                    ValidatorBreakException.NOT_TRUE(!operaFlag, "账号已被注册");
                }

                // 校验帐号是否存在
                operaFlag = asUserMapper.isAccountExists(Conditions.newInstance().putData("account", account).putData("type", UserTypeEnum.STAFF.getId())) > 0;
                ValidatorBreakException.NOT_TRUE(!operaFlag, "账号已被注册");
                operaFlag = asUserMapper.isAccountExists(Conditions.newInstance().putData("account", account).putData("type", UserTypeEnum.SELLER.getId())) > 0;
                ValidatorBreakException.NOT_TRUE(!operaFlag, "账号已被注册");

                if (type == UserTypeEnum.SELLER.getId()) {

                    // 添加后台用户信息
                    String sys_user_id = get32UUID();
                    Conditions sysUser = Conditions.newInstance();
                    sysUser.putData("username", account).putData("name", account).putData("userId", sys_user_id).putData("createBy", getUser().getUSER_ID())
                            .putData("password", CommonUtils.encryptPwd(password, 2)).putData("asShopId", shopId).putData("status", 0)
                            .putData("roleId", asDictParamMapper.findByKeyCode(DictParam.PARAM_SHOPOWNER_ROLE_ID));
                    operaFlag = sysUserMapper.insertSelective(sysUser) == 1;
                    ValidatorBreakException.NOT_TRUE(operaFlag);

                    // 添加商家sys_user记录
                    Conditions user = Conditions.newInstance();
                    user.putData("account", account).putData("password", CommonUtils.encryptPwd(password)).putData("createTime", CommonUtils.getCurDate())
                            .putData("type", UserTypeEnum.SELLER.getId()).putData("sysUserId", sys_user_id);
                    operaFlag = asUserMapper.insertSelective(user) == 1;
                    ValidatorBreakException.NOT_TRUE(operaFlag);

                    // 添加商家管理员信息
                    Conditions shopOwner = Conditions.newInstance();
                    shopOwner.putData("userId", user.getInt("userId")).putData("shopId", shopId).putData("name", name).putData("tel", tel).putData("email", email)
                            .putData("screenDomainId", screenDomainId);
                    operaFlag = asShopOwnerInfoMapper.insertSelective(shopOwner) == 1;
                    ValidatorBreakException.NOT_TRUE(operaFlag);

                    // 添加用户钱包
                    AsWallet wallet = new AsWallet();
                    wallet.setUserId(user.getInt("userId"));
                    wallet.setCreateTime(CommonUtils.getCurDate());
                    operaFlag = asWalletMapper.insertSelective(wallet) == 1;
                    ValidatorBreakException.NOT_TRUE(operaFlag);

                    // 添加员工帐号信息
                    Conditions staffUser = Conditions.newInstance();
                    staffUser.putData("account", account).putData("password", CommonUtils.encryptPwd(password)).putData("createTime", CommonUtils.getCurDate())
                            .putData("type", UserTypeEnum.STAFF.getId());
                    operaFlag = asUserMapper.insertSelective(staffUser) == 1;
                    ValidatorBreakException.NOT_TRUE(operaFlag);

                    // 添加员工信息
                    AsShopStaffInfo staffInfo = new AsShopStaffInfo();
                    staffInfo.setUserId(staffUser.getInt("userId"));
                    staffInfo.setShopUserId(shopOwner.getInt("userId"));
                    staffInfo.setShopId(shopId);
                    staffInfo.setDescp("");
                    staffInfo.setName(name);
                    staffInfo.setSn(get32UUID());
                    staffInfo.setTel(tel);
                    staffInfo.setEmail(email);
                    staffInfo.setScreenDomainId(screenDomainId);
                    operaFlag = asShopStaffInfoMapper.insertSelective(staffInfo) == 1;
                    ValidatorBreakException.NOT_TRUE(operaFlag);

                    // 添加用户钱包
                    AsWallet staffWallet = new AsWallet();
                    staffWallet.setUserId(staffUser.getInt("userId"));
                    staffWallet.setCreateTime(CommonUtils.getCurDate());
                    operaFlag = asWalletMapper.insertSelective(staffWallet) == 1;
                    ValidatorBreakException.NOT_TRUE(operaFlag);
                } else if (type == UserTypeEnum.STAFF.getId()) {

                    // 添加后台用户信息
                    String sys_user_id = get32UUID();
                    Conditions sysUser = Conditions.newInstance();
                    sysUser.putData("username", account).putData("name", account).putData("userId", sys_user_id).putData("createBy", getUser().getUSER_ID())
                            .putData("password", CommonUtils.encryptPwd(password, 2)).putData("asShopId", shopId).putData("status", 0)
                            .putData("roleId", asDictParamMapper.findByKeyCode(DictParam.PARAM_STAFF_ROLE_ID));
                    operaFlag = sysUserMapper.insertSelective(sysUser) == 1;
                    ValidatorBreakException.NOT_TRUE(operaFlag);

                    // 添加员工帐号信息
                    Conditions staffUser = Conditions.newInstance();
                    staffUser.putData("account", account).putData("password", CommonUtils.encryptPwd(password)).putData("createTime", CommonUtils.getCurDate())
                            .putData("type", UserTypeEnum.STAFF.getId()).putData("sysUserId", sys_user_id);
                    operaFlag = asUserMapper.insertSelective(staffUser) == 1;
                    ValidatorBreakException.NOT_TRUE(operaFlag);

                    // 添加员工信息
                    AsShopStaffInfo staffInfo = new AsShopStaffInfo();
                    staffInfo.setUserId(staffUser.getInt("userId"));
                    staffInfo.setShopUserId(0);
                    staffInfo.setShopId(shopId);
                    staffInfo.setDescp("");
                    staffInfo.setName(name);
                    staffInfo.setSn(get32UUID());
                    staffInfo.setTel(tel);
                    staffInfo.setEmail(email);
                    staffInfo.setScreenDomainId(screenDomainId);
                    operaFlag = asShopStaffInfoMapper.insertSelective(staffInfo) == 1;
                    ValidatorBreakException.NOT_TRUE(operaFlag);

                    // 添加用户钱包
                    AsWallet staffWallet = new AsWallet();
                    staffWallet.setUserId(staffUser.getInt("userId"));
                    staffWallet.setCreateTime(CommonUtils.getCurDate());
                    operaFlag = asWalletMapper.insertSelective(staffWallet) == 1;
                    ValidatorBreakException.NOT_TRUE(operaFlag);
                } else {
                    ValidatorBreakException.NOT_TRUE(false, "非法操作！您不是管理员！");
                }

                break;
            case EDIT:

                type = pd.getInt("type");
                name = pd.getParaForString("name");
                email = pd.getParaForString("email");
                tel = pd.getParaForString("tel");
                if (UserTypeEnum.SELLER == UserTypeEnum.GET(pd.getInt("type"))) {
                    AsShopOwnerInfo shopOwner = (AsShopOwnerInfo) asShopOwnerInfoMapper.getOwnerInfo(pd);
                    ValidatorBreakException.NOT_NULL(shopOwner);
                    Conditions owner = Conditions.newInstance().putData("shopOwnerInfoId", shopOwner.getShopOwnerInfoId()).putData("tel", tel)
                            .putData("name", name).putData("email", email);
                    operaFlag = asShopOwnerInfoMapper.updateByPrimaryKeySelective(owner) == 1;
                    ValidatorBreakException.NOT_TRUE(operaFlag);
                }
                if (UserTypeEnum.STAFF == UserTypeEnum.GET(pd.getInt("type"))) {
                    AsShopStaffInfo sta = (AsShopStaffInfo) asShopStaffInfoMapper.getStaffInfo(pd);
                    ValidatorBreakException.NOT_NULL(sta);
                    Conditions staff = Conditions.newInstance().putData("shopStaffInfoId", sta.getShopStaffInfoId()).putData("tel", tel).putData("name", name)
                            .putData("email", email);
                    operaFlag = asShopStaffInfoMapper.updateByPrimaryKeySelective(staff) == 1;
                    ValidatorBreakException.NOT_TRUE(operaFlag);
                }
                break;
            default:
                ValidatorBreakException.NOT_NULL(null);
        }

        return renderSuccess();
    }

    /**
     * 后台管理帐号列表
     *
     * @param c
     * @return
     * @author yangyanchao
     * @date 2016年11月2日
     */
    public Object getAdminAccountList(BaseController c) {

        initialized(c);
        pd.put("type", "3");
        pageQueryModule(asUserMapper, "getAdminUserWhitConditions", true);

        return renderView(PAGE_PATH + "admin_account_list");
    }

    /**
     * 运营管理帐号列表
     *
     * @param c
     * @return
     * @author huangxw
     * @date 2017年04月05日
     */
    public Object getOperaAdminAccountList(BaseController c) {

        initialized(c);
        pd.put("type", "4");
        pageQueryModule(asUserMapper, "getAdminUserWhitConditions", true);

        return renderView(PAGE_PATH + "opear_admin_account_list");
    }

    /**
     * 添加管理员H5
     *
     * @param c
     * @return
     * @author yangyanchao
     * @date 2016年11月2日
     */
    public Object addAdminAccountPage(BaseController c) {

        initialized(c);

        addViewData("action", pd.getParaForPositiveInteger("action"));

        return renderView(PAGE_PATH + "save_admin_account");
    }

    /**
     * 运营管理帐号添加
     *
     * @param c
     * @return
     * @author huangxw
     * @date 2017年04月05日
     */
    public Object addOperaAdminAccountPage(BaseController c) {

        initialized(c);

        addViewData("action", pd.getParaForPositiveInteger("action"));

        return renderView(PAGE_PATH + "save_opera_admin_account");
    }

    /**
     * 添加管理员帐号
     *
     * @param c
     * @return
     * @author yangyanchao
     * @date 2016年11月2日
     */
    public Object saveAdminAccount(BaseController c) {

        initialized(c);

        String name = pd.getParaForString("name");
        String tel = pd.getParaForString("tel");
        String account = pd.getParaForString("account");
        String password = pd.getParaForString("password");
        String userType = (String) pd.get("userType");

        operaFlag = asUserMapper.isAccountExists(Conditions.newInstance().putData("account", account).putData("type", UserTypeEnum.ADMIN.getId())) > 0;
        ValidatorBreakException.NOT_TRUE(!operaFlag, "账号已被注册");

        // 添加后台用户信息
        String sys_user_id = get32UUID();
        Conditions sysUser = Conditions.newInstance();
        String uType = userType.compareTo("1") == 0 ? DictParam.PARAM_ADMIN_ROLE_ID : (userType.compareTo("2") == 0 ? DictParam.PARAM_OPERATION_MANAGER_ROLE_ID
                : DictParam.PARAM_CURRENCY_MANAGER_ROLE_ID);

        sysUser.putData("username", account).putData("name", account).putData("userId", sys_user_id).putData("createBy", getUser().getUSER_ID())
                .putData("password", CommonUtils.encryptPwd(password, 2)).putData("status", 0).putData("skin", "skin-2")
                .putData("roleId", asDictParamMapper.findByKeyCode(uType));
        operaFlag = sysUserMapper.insertSelective(sysUser) == 1;
        ValidatorBreakException.NOT_TRUE(operaFlag);

        // 添加账号
        AsUser user = new AsUser();
        user.setAccount(account);
        user.setCreateTime(CommonUtils.getCurDate());
        user.setPassword(CommonUtils.encryptPwd(password, 2));
        user.setStatus(0);
        user.setSysUserId(sys_user_id);
        user.setType(userType.compareTo("1") == 0 ? UserTypeEnum.ADMIN.getId() : userType.compareTo("2") == 0 ? UserTypeEnum.OPERATIONMANAGER.getId()
                : UserTypeEnum.CURRENCY.getId());
        operaFlag = asUserMapper.insertSelective(user) == 1;
        ValidatorBreakException.NOT_TRUE(operaFlag);

        // 添加后台管理员信息表
        AsAdminInfo adminInfo = new AsAdminInfo();
        adminInfo.setAdminName(name);
        adminInfo.setAdminPhone(tel);
        adminInfo.setCreateTime(CommonUtils.getCurDate());
        adminInfo.setRoleId(sysUser.get("roleId").toString());
        adminInfo.setStatus(0);
        adminInfo.setUserId(user.getUserId());
        operaFlag = asAdminInfoMapper.insertSelective(adminInfo) == 1;
        ValidatorBreakException.NOT_TRUE(operaFlag);

        // 添加钱包记录
        AsWallet wallet = new AsWallet();
        wallet.setUserId(user.getUserId());
        wallet.setCreateTime(CommonUtils.getCurDate());
        operaFlag = asWalletMapper.insertSelective(wallet) == 1;
        ValidatorBreakException.NOT_TRUE(operaFlag);

        return renderSuccess();
    }

    /**
     * 删除帐号
     *
     * @param c
     * @return
     * @author yangyanchao
     * @date 2016年11月1日
     */
    public Object delete(BaseController c) {

        initialized(c);

        int userId = pd.getParaForPositiveInteger("userId");

        // 判断该帐号是否特殊管理员
        Conditions user = (Conditions) asUserMapper.findByUserId(userId);
        ValidatorBreakException.NOT_NULL(user);

        UserTypeEnum uType = UserTypeEnum.GET(user.getInt("type"));
        if (UserTypeEnum.ADMIN == uType && user.getStr("account").equals("admin")) {

            ValidatorBreakException.NOT_TRUE(false, "无法删除该帐号");
        } else if (UserTypeEnum.SELLER == uType) {

            Conditions conditions = Conditions.newInstance().putData("account", user.getStr("account")).putData("type", UserTypeEnum.STAFF.getId());
            Conditions staff = (Conditions) asUserMapper.findByAccountAndType(conditions);
            ValidatorBreakException.NOT_NULL(user);

            staff.putData("status", 1);
            operaFlag = asUserMapper.updateByPrimaryKeySelective(staff) == 1;
        }

        // 删除账号表
        operaFlag = asUserMapper.deleteByUserId(userId) == 1;
        ValidatorBreakException.NOT_TRUE(operaFlag, "数据信息已过期，请刷新后重试");

        return renderSuccess();
    }

    /**
     * 更改帐号状态(帐号 禁用&恢复)
     *
     * @return
     * @author yangyanchao
     * @date 2016年9月2日
     */
    public Object accountStatus_Tx(BaseController c) {

        initialized(c);

        int status = pd.getInt("status");
        int userId = pd.getInt("userId");

        Conditions user = (Conditions) asUserMapper.findByUserId(userId);
        ValidatorBreakException.NOT_NULL(user);

        int type = user.getInt("type");
        if (UserTypeEnum.SELLER == UserTypeEnum.GET(type)) {

            // 更新商家管理员帐号禁用状态
            Conditions shopOwner = asShopOwnerInfoMapper.findByUserId(userId);
            ValidatorBreakException.NOT_NULL(shopOwner);
            shopOwner.putData("status", status);
            operaFlag = asShopOwnerInfoMapper.updateByPrimaryKeySelective(shopOwner) == 1;
            ValidatorBreakException.NOT_TRUE(operaFlag, "数据已过期，请刷新后重试");

            //如果商场账号被启用 则一起启用该商场下的同名员工账号
            if(status==0){
                AsUser asUser =  asUserMapper.getUserByUserId(userId);
                AsUser asUserStaff = asUserMapper.getByAccountAndType(asUser.getAccount());
                if(null != asUserStaff){
                    Conditions staffInfo = (Conditions) asShopStaffInfoMapper.findByUserId(asUserStaff.getUserId());
                    ValidatorBreakException.NOT_NULL(staffInfo);
                    staffInfo.putData("status", status);
                    operaFlag = asShopStaffInfoMapper.updateByPrimaryKeySelective(staffInfo) == 1;
                    ValidatorBreakException.NOT_TRUE(operaFlag, "数据已过期，请刷新后重试");
                }
            }
            //如果商场账号被禁用 则一起禁用该商场下的所属员工账号
            if(status==1){
                List<AsShopStaffInfo> stafflist = asShopStaffInfoMapper.getStaffInfoByShopId(shopOwner.getInt("shopId"));
                AsShopStaffInfo asShopStaffInfo = new AsShopStaffInfo();
                asShopStaffInfo.setShopId(shopOwner.getInt("shopId"));
                asShopStaffInfo.setStatus(status);
                operaFlag = asShopStaffInfoMapper.updateStatusByShopId(asShopStaffInfo) == stafflist.size();
                ValidatorBreakException.NOT_TRUE(operaFlag, "数据已过期，请刷新后重试");
            }
        } else if (UserTypeEnum.STAFF == UserTypeEnum.GET(type)) {

            // 更新所属商家管理员的员工管理员帐号禁用状态
            Conditions conditions = Conditions.newInstance().putData("account", user.getStr("account")).putData("type", UserTypeEnum.STAFF.getId());
            Conditions staff = (Conditions) asUserMapper.findByAccountAndType(conditions);
            ValidatorBreakException.NOT_NULL(user);

            Conditions staffInfo = (Conditions) asShopStaffInfoMapper.findByUserId(staff.getInt("userId"));
            ValidatorBreakException.NOT_NULL(staffInfo);
            staffInfo.putData("status", status);
            operaFlag = asShopStaffInfoMapper.updateByPrimaryKeySelective(staffInfo) == 1;
            ValidatorBreakException.NOT_TRUE(operaFlag, "数据已过期，请刷新后重试");
        } else if (UserTypeEnum.STAFF == UserTypeEnum.GET(type)) {

            // 更新员工管理员帐号禁用状态
            Conditions shopStaff = (Conditions) asShopStaffInfoMapper.findByUserId(userId);
            ValidatorBreakException.NOT_NULL(shopStaff);
            shopStaff.put("status", status);
            operaFlag = asShopStaffInfoMapper.updateByPrimaryKeySelective(shopStaff) == 1;
            ValidatorBreakException.NOT_TRUE(operaFlag, "数据已过期，请刷新后重试");
        } else if (UserTypeEnum.ADMIN == UserTypeEnum.GET(type)) {

            // 无法删除后台管理员唯一帐号
            if (user.getStr("account").equals("admin")) {
                ValidatorBreakException.NOT_TRUE(false, "无法删除该帐号");
            }
            // 更新后台管理员帐号禁用状态
            AsAdminInfo adminInfo = (AsAdminInfo) asAdminInfoMapper.findByUserId(userId);
            ValidatorBreakException.NOT_NULL(adminInfo);
            adminInfo.setStatus(status);
            operaFlag = asAdminInfoMapper.updateByPrimaryKeySelective(adminInfo) == 1;
            ValidatorBreakException.NOT_TRUE(operaFlag);
        } else if (UserTypeEnum.OPERATIONMANAGER == UserTypeEnum.GET(type)) {

            // 更新运营管理员帐号禁用状态
            AsAdminInfo adminInfo = (AsAdminInfo) asAdminInfoMapper.findByUserId(userId);
            ValidatorBreakException.NOT_NULL(adminInfo);
            adminInfo.setStatus(status);
            operaFlag = asAdminInfoMapper.updateByPrimaryKeySelective(adminInfo) == 1;
            ValidatorBreakException.NOT_TRUE(operaFlag);

        } else if (UserTypeEnum.CURRENCY == UserTypeEnum.GET(type)) {
            // 更新运营管理员帐号禁用状态
            AsAdminInfo adminInfo = (AsAdminInfo) asAdminInfoMapper.findByUserId(userId);
            ValidatorBreakException.NOT_NULL(adminInfo);
            adminInfo.setStatus(status);
            operaFlag = asAdminInfoMapper.updateByPrimaryKeySelective(adminInfo) == 1;
            ValidatorBreakException.NOT_TRUE(operaFlag);

        } else if (UserTypeEnum.USER == UserTypeEnum.GET(type)) {
            // 更新会员帐号禁用状态
            AsUser asUser = asUserMapper.getUserByUserId(userId);
            ValidatorBreakException.NOT_NULL(asUser);
            asUser.setStatus(status);
            operaFlag = asUserMapper.updateByPrimaryKeySelective(asUser) == 1;
            ValidatorBreakException.NOT_TRUE(operaFlag);

        } else {
            ValidatorBreakException.NOT_NULL(null);
        }

        return renderSuccess();
    }

    public String CheckFile(String img) {
        String msg = "";
        if (StringUtils.isNotBlank(req.getParameter(img))) {
            String arr[] = String.valueOf(pd.get(img)).split(",");
            byte[] bytes = Base64.decode(arr[1]);
            BufferedImage buffImage;
            try {
                InputStream io = new ByteArrayInputStream(bytes);
                buffImage = javax.imageio.ImageIO.read(io);

                if (buffImage.getWidth() > CommonStr.IMG_SHOP_LOG_WIDTH || buffImage.getHeight() > CommonStr.IMG_SHOP_LOG_HEIGHT) {
                    if (img.endsWith("0")) {
                        msg = "商户Logo图片尺寸大小超过限制！请上传" + CommonStr.IMG_SHOP_LOG_WIDTH + "px" + "*" + CommonStr.IMG_SHOP_LOG_HEIGHT + "px 的图片!";
                    }
                    if (img.endsWith("1")) {
                        msg = "商户背景图图片尺寸大小超过限制！请上传" + CommonStr.IMG_SHOP_LOG_WIDTH + "px" + "*" + CommonStr.IMG_SHOP_LOG_HEIGHT + "px 的图片!";
                    }
                    if (img.endsWith("2")) {
                        msg = "商户二维码图片尺寸大小超过限制！请上传" + CommonStr.IMG_SHOP_LOG_WIDTH + "px" + "*" + CommonStr.IMG_SHOP_LOG_HEIGHT + "px 的图片!";
                    }

                }

                buffImage = javax.imageio.ImageIO.read(io);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return msg;
    }

    /**
     * 员工列表
     *
     * @return
     * @author huangxw
     * @date 2017年3月13日
     */
    public Object getStaffListAll(BaseController accountController) {
        initialized(c);
        List<Object> staffList = asUserMapper.getStaffListAll(pd);
        addViewData("pd", pd);
        addViewData("staffList", staffList);
        return renderView(PAGE_PATH + "shop_staff_account_list");

    }

    /**
     * 管理帐号列表
     *
     * @param c
     * @return
     * @author huangxw
     * @date 2017年04月05日
     */
    public Object getAccountList(BaseController c) {

        initialized(c);
        pd.put("type", "5");
        pageQueryModule(asUserMapper, "getAdminUserWhitConditions", true);

        return renderView(PAGE_PATH + "account_list");
    }

    /**
     * 管理帐号添加
     *
     * @param c
     * @return
     * @author huangxw
     * @date 2017年04月05日
     */
    public Object addAccountPage(BaseController c) {

        initialized(c);

        addViewData("action", pd.getParaForPositiveInteger("action"));
        addViewData("roleList", sysRoleMapper.getRoleAll());
        return renderView(PAGE_PATH + "save_account");
    }

    /**
     * ##################################################会员管理#######################################################
     */

    /**
     * 获取会员账号收货地址列表
     *
     * @param c
     * @return
     * @author tgb
     * @date 2017年05月12日
     */
    public Object getAccountAddressList(BaseController c) {

        initialized(c);

        List<AsProvince> provinceList = provinceMapper.selectByAttr(pd);
        List<AsCity> cityList = asCityMapper.selectByAttr(null);
        List<AsArea> areaList = asAreaMapper.selectByAttr(null);

        addViewData("provinceList", provinceList);
        addViewData("cityList", cityList);
        addViewData("areaList", areaList);

        pageQueryModule(userAddressMapper, "listAll", true);
        return renderView(PAGE_PATH + "accountAddressList");
    }

    /**
     * 添加会员账号页面
     *
     * @return c
     * @author tgb
     * @date 2017年05月12日
     */
    public Object saveAccountMemberPage(BaseController c) {

        initialized(c);

        OperaEnum action = OperaEnum.get(pd.getInt("action"));

        switch (action) {
            case ADD:
                List<AsProvince> provinceList = provinceMapper.selectByAttr(pd);
                List<AsCity> cityList = asCityMapper.selectByAttr(null);
                JSONArray cityListJson = JSONArray.fromObject(cityList);
                List<AsArea> areaList = asAreaMapper.selectByAttr(null);
                JSONArray areaListJson = JSONArray.fromObject(areaList);

                addViewData("provinceList", provinceList);
                addViewData("cityList", cityListJson);
                addViewData("areaList", areaListJson);
                addViewData("pd", pd);

                break;
            default:
                ValidatorBreakException.NOT_NULL(null);
        }
        addViewData("action", action.getId());
        return renderView(PAGE_PATH + "saveAccountMember");
    }

    /**
     * 异步检查检查会员账号是否已经存在
     *
     * @param c
     * @return
     * @author tgb
     * @date 2017年05月12日
     */
    public Object accountMemberIsExist(BaseController c) {
        initialized(c);
        String account = pd.getParaForString("account");
        int data = asUserMapper.accountMemberIsExist(account);
        if (data > 0) {
            return true;
        }
        return false;
    }

    /**
     * 添加会员操作
     *
     * @return c
     * @author tgb
     * @date 2017年05月12日
     */
    public Object saveAccountMember0pera(BaseController c) {

        initialized(c);

        OperaEnum action = OperaEnum.get(pd.getInt("action"));

        switch (action) {
            case ADD:

                String account = pd.getParaForString("account");
                int data = asUserMapper.accountMemberIsExist(account);
                if (data > 0) {
                    ValidatorBreakException.NOT_TRUE(false, "该用户账号已经存在");
                }
                String password = pd.get("password") == null ? "" : pd.get("password").toString();
                if (password != "") {
                    password = "123456";
                }
                //保存会员
                AsUser user = new AsUser();
                user.setAccount(account);
                user.setPassword(MD5Util.GetMD5Code(password));
                user.setCreateTime(DateUtil.getTime());
                user.setStatus(0);
                user.setType(0);
                operaFlag = asUserMapper.insertSelective(user) == 1;
                ValidatorBreakException.NOT_TRUE(operaFlag);

                //保存会员信息
                String nickName = pd.getParaForString("nickName");
                Integer provinceId = pd.getParaForStringOrNull("provinceId") == "" ? null : pd.getInt("provinceId");
                Integer cityId = pd.getParaForStringOrNull("cityId") == "" ? null : pd.getInt("cityId");
                Integer areaId = pd.getParaForStringOrNull("areaId") == "" ? null : pd.getInt("areaId");
                String address = pd.getParaForStringOrNull("address") == "" ? null : pd.getParaForString("address");
                Integer sex = pd.getInt("sex");
                uploadModule(UploadPathEnum.USERS, "avata");
                String avata = pd.get("avata") == null ? "" : pd.get("avata").toString();

                AsUserInfo info = new AsUserInfo();
                info.setUserId(user.getUserId());
                info.setNickName(nickName);
                info.setProvinceId(provinceId);
                info.setCityId(cityId);
                info.setAreaId(areaId);
                info.setAddress(address);
                info.setSex(sex);
                info.setAvata(avata);
                info.setMyInvite(RandomCodeUtil.idToCode(user.getUserId(), 6));

                operaFlag = userInfoMapper.insertSelective(info) == 1;
                ValidatorBreakException.NOT_TRUE(operaFlag);

                AsWallet wallet = new AsWallet();
                wallet.setUserId(user.getUserId());
                wallet.setCreateTime(DateUtil.getTime());
                wallet.setMoney(new BigDecimal("0"));
                wallet.setIntegration(new BigDecimal("0"));

                operaFlag = asWalletMapper.insertSelective(wallet) == 1;
                ValidatorBreakException.NOT_TRUE(operaFlag);
                break;

            default:
                ValidatorBreakException.NOT_TRUE(false);
        }
        return renderSuccess();
    }

    /**
     * 会员收货地址页面中转控制
     *
     * @return c
     * @author tgb
     * @date 2017年05月12日
     */
    public Object accountAddressPage(BaseController c) {

        initialized(c);

        OperaEnum action = OperaEnum.get(pd.getInt("action"));

        switch (action) {
            case ADD:
                List<AsProvince> provinceList = provinceMapper.selectByAttr(pd);
                List<AsCity> cityList = asCityMapper.selectByAttr(null);
                JSONArray cityListJson = JSONArray.fromObject(cityList);
                List<AsArea> areaList = asAreaMapper.selectByAttr(null);
                JSONArray areaListJson = JSONArray.fromObject(areaList);

                addViewData("provinceList", provinceList);
                addViewData("cityList", cityListJson);
                addViewData("areaList", areaListJson);
                addViewData("pd", pd);

                break;
            case EDIT:
                provinceList = provinceMapper.selectByAttr(null);
                cityList = asCityMapper.selectByAttr(null);
                cityListJson = JSONArray.fromObject(cityList);
                areaList = asAreaMapper.selectByAttr(null);
                areaListJson = JSONArray.fromObject(areaList);

                addViewData("provinceList", provinceList);
                addViewData("cityList", cityListJson);
                addViewData("areaList", areaListJson);

                int userAddressId = pd.getInt("userAddressId");
                AsUserAddress address = userAddressMapper.selectByPrimaryKey(userAddressId);
                addViewData("address", address);

                break;

            default:
                ValidatorBreakException.NOT_NULL(null);
        }
        addViewData("action", action.getId());
        int userId = pd.getInt("userId");
        addViewData("userId", userId);
        return renderView(PAGE_PATH + "saveAccountAddress");
    }

    /**
     * 会员收货地址操作
     *
     * @return c
     * @author tgb
     * @date 2017年05月12日
     */
    public Object accountAddressOpera(BaseController c) {

        initialized(c);

        OperaEnum action = OperaEnum.get(pd.getInt("action"));
        Integer userId = pd.getInt("userId");

        switch (action) {
            case ADD:

                Integer provinceId = pd.getInt("provinceId");
                Integer cityId = pd.getInt("cityId");
                Integer areaId = pd.getInt("areaId");

                String phone = pd.getParaForString("phone");
                String address = pd.getParaForString("address");
                String receiver = pd.getParaForString("receiver");

                AsUserAddress userAddress = new AsUserAddress();
                userAddress.setUserId(userId);
                userAddress.setProvinceId(provinceId);
                userAddress.setCityId(cityId);
                userAddress.setAreaId(areaId);
                userAddress.setPhone(phone);
                userAddress.setAddress(address);
                userAddress.setReceiver(receiver);
                userAddress.setIsDefault(0);

                operaFlag = userAddressMapper.insertSelective(userAddress) == 1;
                ValidatorBreakException.NOT_TRUE(operaFlag);

                break;

            case EDIT:
                Integer userAddressId = pd.getInt("userAddressId");
                userAddress = userAddressMapper.selectByPrimaryKey(userAddressId);
                ValidatorBreakException.NOT_NULL(userAddress);

                provinceId = pd.getInt("provinceId");
                cityId = pd.getInt("cityId");
                areaId = pd.getInt("areaId");
                phone = pd.getParaForString("phone");
                address = pd.getParaForString("address");
                receiver = pd.getParaForString("receiver");
                userAddress.setProvinceId(provinceId);
                userAddress.setCityId(cityId);
                userAddress.setAreaId(areaId);
                userAddress.setPhone(phone);
                userAddress.setAddress(address);
                userAddress.setReceiver(receiver);
                operaFlag = userAddressMapper.updateByPrimaryKeySelective(userAddress) == 1;
                ValidatorBreakException.NOT_TRUE(operaFlag);

                break;
            case DELETE:
                userAddressId = pd.getInt("userAddressId");
                operaFlag = userAddressMapper.deleteByPrimaryKey(userAddressId) == 1;
                ValidatorBreakException.NOT_TRUE(operaFlag);
                break;
            default:
                ValidatorBreakException.NOT_TRUE(false);
        }
        return renderSuccess();
    }

    /**
     * 设置会员默认收货地址
     *
     * @param c
     * @return
     * @author tgb 创建时间：2017年05月12日
     * @version 1.0
     */
    public Object updateAddress(BaseController c) {

        initialized(c);
        int userAddressId = pd.getInt("userAddressId");
        int userId = pd.getInt("userId");
        List<AsUserAddress> userAddressList = userAddressMapper.getAddressListByUserId(userId);
        if (!userAddressList.isEmpty() && userAddressList.size() > 0) {
            for (AsUserAddress a : userAddressList) {
                if (a.getUserAddressId().equals(userAddressId)) {
                    a.setIsDefault(1);
                    operaFlag = userAddressMapper.updateByPrimaryKeySelective(a) == 1;
                } else {
                    a.setIsDefault(0);
                    operaFlag = userAddressMapper.updateByPrimaryKeySelective(a) == 1;
                }
            }
        }
        ValidatorBreakException.NOT_TRUE(operaFlag);
        return renderSuccess();
    }

    /**
     * 获取会员银行卡列表
     *
     * @param c
     * @return
     * @author tgb
     * @date 2017年05月12日
     */
    public Object getAccountBankBindList(BaseController c) {

        initialized(c);
        pageQueryModule(bankBindMapper, "listAll", true);
        return renderView(PAGE_PATH + "accountBankBindList");
    }

    /**
     * 解绑会员银行卡
     *
     * @param c
     * @return
     * @author tgb 创建时间：2017年05月12日
     * @version 1.0
     */
    public Object unAccountBankBind(BaseController c) {

        initialized(c);
        int bankBindId = pd.getInt("bankBindId");
        AsBankBind bankBind = bankBindMapper.selectByPrimaryKey(bankBindId);
        ValidatorBreakException.NOT_NULL(bankBind);
        bankBind.setStatus(1);
        operaFlag = bankBindMapper.updateByPrimaryKeySelective(bankBind) == 1;
        ValidatorBreakException.NOT_TRUE(operaFlag);
        return renderSuccess();
    }

    /**
     * 获取会员余额列表
     *
     * @param c
     * @return
     * @author tgb
     * @date 2017年05月12日
     */
    public Object getAccountCapitalList(BaseController c) {

        initialized(c);
        List<?> list = pageQueryModule(capitalMapper, "listAllMemberCapitals", true);
        if (list.isEmpty() && list.size() == 0) {
            int userId = pd.getInt("userId");
            AsWallet wallet = asWalletMapper.findByUserId(userId);
            addViewData("wallet", wallet);
        }

        return renderView(PAGE_PATH + "accountCapitalList");
    }

    /**
     * 查看处理描述
     *
     * @param c
     * @return
     * @author tgb
     * @date 2017年05月12日
     */
    public Object reason(BaseController c) {

        initialized(c);

        int capitalId = pd.getInt("capitalId");
        AsCapital capital = (AsCapital) capitalMapper.selectByPrimaryKey(capitalId);
        ValidatorBreakException.NOT_NULL(capital, 1001, "系统繁忙", 1);
        addViewData("capital", capital);
        return renderView(PAGE_PATH + "reason");
    }

    /**
     * 判断会员账号是否绑定银行卡
     *
     * @param c
     * @return
     * @author tgb
     * @date 2017年05月12日
     */
    public Object isBankBind(BaseController c) {
        initialized(c);
        int userId = pd.getInt("userId");
        int result = bankBindMapper.isBankBind(userId);
        if (result > 0) {
            return true;
        }
        return false;
    }

    /**
     * 会员账号提现申请页面
     *
     * @param c
     * @return
     * @author tgb
     * @date 2017年05月15日
     */
    public Object accountDepositPage(BaseController c) {

        initialized(c);

        OperaEnum action = OperaEnum.get(pd.getInt("action"));

        switch (action) {
            case ADD:
                int userId = pd.getInt("userId");
                List<AsBankBind> bankBindList = bankBindMapper.selectAllByUserId(userId);
                JSONArray bankBindListJson = JSONArray.fromObject(bankBindList);
                AsWallet wallet = asWalletMapper.findByUserId(userId);
                addViewData("wallet", wallet);
                addViewData("list", bankBindList);
                addViewData("bankAccount", bankBindList.get(bankBindList.size() - 1).getBankAccount());
                addViewData("listJson", bankBindListJson);

                String needServiceCharge = asDictParamMapper.findByKeyCode(DictParam.PARAM_CAPITAL_NEED_SERVICE_CHARGE);
                String serviceCharge = asDictParamMapper.findByKeyCode(DictParam.PARAM_CAPITAL_SET_SERVICE_CHARGE);
                if (needServiceCharge.equals("0")) {
                    serviceCharge = String.valueOf(ArithmeticUtil.newInstance(0));
                }
                String min = asDictParamMapper.findByKeyCode(DictParam.PARAM_CAPITAL_MONEY_MIN);
                String max = asDictParamMapper.findByKeyCode(DictParam.PARAM_CAPITAL_MONEY_MAX);
                String surplusTimes = asDictParamMapper.findByKeyCode(DictParam.PARAM_CAPITAL_TIMES_DAY);
                int counts = capitalMapper.countCapitalByDay(Conditions.newInstance().putData("userId", userId));
                surplusTimes = String.valueOf((int) ArithmeticUtil.SUBTRACT(Double.parseDouble(surplusTimes), Double.parseDouble(String.valueOf(counts))));

                addViewData("needServiceCharge", needServiceCharge);
                addViewData("serviceCharge", serviceCharge);
                addViewData("min", min);
                addViewData("max", max);
                addViewData("surplusTimes", surplusTimes);

                break;
            default:
                ValidatorBreakException.NOT_NULL(null);
        }
        addViewData("action", action.getId());
        return renderView(PAGE_PATH + "saveAccountDeposit");
    }

    /**
     * 会员账号提现申请操作
     *
     * @return c
     * @author tgb
     * @date 2017年05月15日
     */
    public Object accountDepositOpera(BaseController c) {

        initialized(c);

        OperaEnum action = OperaEnum.get(pd.getInt("action"));

        switch (action) {
            case ADD:

                int userId = pd.getInt("userId");
                int bankBindId = pd.getInt("bankBindId");
                String moneyTemp = pd.getParaForString("money");
                BigDecimal money = new BigDecimal(moneyTemp);

                double amountDouble = money.doubleValue();
                ValidatorBreakException.NOT_TRUE(amountDouble > 0, "金额不合法");

                // 判断提现剩余次数
                int platformCounts = CommonUtils.o2i(asDictParamMapper.findByKeyCode(DictParam.PARAM_CAPITAL_TIMES_DAY));
                int counts = capitalMapper.countCapitalByDay(Conditions.newInstance().putData("userId", userId));
                ValidatorBreakException.NOT_TRUE(counts < platformCounts, "已超过当天可提现次数");

                // 是否需要手续费
                String needServiceCharge = asDictParamMapper.findByKeyCode(DictParam.PARAM_CAPITAL_NEED_SERVICE_CHARGE);
                BigDecimal serviceCharge = new BigDecimal(asDictParamMapper.findByKeyCode(DictParam.PARAM_CAPITAL_SET_SERVICE_CHARGE));
                if (needServiceCharge.equals("0")) {
                    serviceCharge = BigDecimal.ZERO;
                }

                // 提现金额不能小于手续费
                double minMoney = CommonUtils.o2d(asDictParamMapper.findByKeyCode(DictParam.PARAM_CAPITAL_MONEY_MIN));
                ValidatorBreakException.NOT_TRUE(amountDouble > serviceCharge.doubleValue(), "提现金额不能小于手续费");

                // 判断提现金额,是否大于最低限额
                ValidatorBreakException.NOT_TRUE(amountDouble >= minMoney, "最低提现" + minMoney + "元");

                // 判断最大金额
                double maxMoney = CommonUtils.o2d(asDictParamMapper.findByKeyCode(DictParam.PARAM_CAPITAL_MONEY_MAX));
                ValidatorBreakException.NOT_TRUE(ArithmeticUtil.SUBTRACT(amountDouble, maxMoney) <= 0, "最高提现金额 " + maxMoney + " 元");

                // 判断钱包是否够钱
                AsWallet wallet = (AsWallet) asWalletMapper.findByUserIdWithLock(userId);
                double oldWalletMoney = wallet.getMoney().doubleValue();
                ValidatorBreakException.NOT_TRUE(ArithmeticUtil.SUBTRACT(oldWalletMoney, amountDouble) >= 0, "余额不足");

                // 冻结钱包金额
                double newWalletMoney = ArithmeticUtil.SUBTRACT(oldWalletMoney, amountDouble);
                wallet.setMoney(ArithmeticUtil.newInstance(newWalletMoney));
                wallet.setUpdateTime(DateUtil.getTime());
                operaFlag = asWalletMapper.updateByPrimaryKeySelective(wallet) == 1;
                ValidatorBreakException.NOT_TRUE(operaFlag, "系统错误,请联系客服");

                AsCapital capital = new AsCapital();
                capital.setUserId(userId);
                capital.setBankBindId(bankBindId);
                capital.setMoney(money);
                capital.setServiceCharge(serviceCharge);
                capital.setCreateTime(DateUtil.getTime());
                capital.setStatus(0);
                capital.setAdminId(Integer.parseInt(getUser().getNewUserId()));

                operaFlag = capitalMapper.insertSelective(capital) == 1;
                ValidatorBreakException.NOT_TRUE(operaFlag, "系统错误,请联系客服");

                break;

            default:
                ValidatorBreakException.NOT_TRUE(false);
        }
        return renderSuccess();
    }

    /**
     * 会员账号提现申请页面
     *
     * @param c
     * @return
     * @author tgb
     * @date 2017年05月15日
     */
    public Object accountIntegrationPage(BaseController c) {

        initialized(c);

        OperaEnum action = OperaEnum.get(pd.getInt("action"));
        int userId = pd.getInt("userId");

        switch (action) {
            case EDIT:

                Integer integration = asWalletMapper.getIntegration(userId);
                AsUser user = asUserMapper.getUserByUserId(userId);
                addViewData("integration", integration);
                addViewData("user", user);
                break;
            default:
                ValidatorBreakException.NOT_NULL(null);
        }
        addViewData("action", action.getId());
        return renderView(PAGE_PATH + "saveAccountIntegration");
    }

    /**
     * 会员账号我的积分操作
     *
     * @return c
     * @author tgb
     * @date 2017年05月15日
     */
    public Object accountIntegrationOpera(BaseController c) {

        initialized(c);

        OperaEnum action = OperaEnum.get(pd.getInt("action"));
        int userId = pd.getInt("userId");

        switch (action) {
            case EDIT:

                String integrationTemp = pd.getParaForString("integration");
                BigDecimal integration = new BigDecimal(integrationTemp);

                AsWallet wallet = (AsWallet) asWalletMapper.findByUserIdWithLock(userId);

                // 计算积分加减，积分明细
                double integrationDouble = integration.doubleValue();
                double oldWalletIntegration = wallet.getIntegration().doubleValue();
                double newWalletIntegration = ArithmeticUtil.SUBTRACT(oldWalletIntegration, integrationDouble);

                BigDecimal integration2 = ArithmeticUtil.newInstance(newWalletIntegration);
                if (newWalletIntegration != 0) {
                    AsUserPoint point = new AsUserPoint();
                    point.setUserId(userId);
                    point.setPointId(userId);
                    point.setPointType(10);
                    point.setCreateTime(DateUtil.getTime());
                    if (newWalletIntegration > 0) {
                        point.setRemark("后台操作减");
                        point.setPoint(-integration2.intValue());
                    } else if (newWalletIntegration < 0) {
                        point.setRemark("后台操作加");
                        point.setPoint(-integration2.intValue());
                    }

                    operaFlag = userPointMapper.insertSelective(point) == 1;
                    ValidatorBreakException.NOT_TRUE(operaFlag);

                    //更新积分
                    wallet.setIntegration(integration);
                    wallet.setUpdateTime(DateUtil.getTime());
                    operaFlag = asWalletMapper.updateByPrimaryKeySelective(wallet) == 1;
                    ValidatorBreakException.NOT_TRUE(operaFlag);
                }
                AsUser user = asUserMapper.getUserByUserId(userId);
                addViewData("integration", integration);
                addViewData("user", user);

                break;

            default:
                ValidatorBreakException.NOT_TRUE(false);
        }

        addViewData("action", action.getId());
        return renderView(PAGE_PATH + "saveAccountIntegration");
    }

    /**
     * 获取会员账户积分明细列表
     *
     * @param c
     * @return
     * @author tgb
     * @date 2017年05月12日
     */
    public Object getPointDetailList(BaseController c) {

        initialized(c);
        pageQueryModule(userPointMapper, "listAll", true);
        return renderView(PAGE_PATH + "pointDetailList");
    }

    /**
     * 获取会员账户积分兑换记录列表
     *
     * @param c
     * @return
     * @author tgb
     * @date 2017年05月12日
     */
    public Object getExchangeRecordList(BaseController c) {

        initialized(c);
        pageQueryModule(userPointMapper, "getExchangeRecordList", true);
        return renderView(PAGE_PATH + "exchangeRecordList");
    }

    /**
     * @param @param  accountController
     * @param @return
     * @return Object
     * @throws
     * @Title:
     * @Description: 获取个人信息
     */
    public Object getAccountInfoList(BaseController c) {
        initialized(c);
        List<AsProvince> provinceList = provinceMapper.selectByAttr(pd);
        List<AsCity> cityList = asCityMapper.selectByAttr(null);
        JSONArray cityListJson = JSONArray.fromObject(cityList);
        List<AsArea> areaList = asAreaMapper.selectByAttr(null);
        JSONArray areaListJson = JSONArray.fromObject(areaList);
        addViewData("provinceList", provinceList);
        addViewData("cityList", cityListJson);
        addViewData("areaList", areaListJson);
        AsUserInfo user = userInfoMapper.getUserInfoList(pd);
        if (user.getBirthday() == null || "".equals(user.getBirthday()))
            user.setBirthday("1918-01-01");
        addViewData("userInfo", user);
        return renderView(PAGE_PATH + "userInfo");

    }

    /**
     * @param @param  accountController
     * @param @return
     * @return Object
     * @throws
     * @Title: saveShopMemberInfo
     * @Description: TODO
     */
    public Object updateShopMemberInfo(BaseController c) {
        initialized(c);
        AsUserInfo user = userInfoMapper.selectByPrimaryKey(pd.getIntegerRequired("userInfoId"));
        Integer provinceId = pd.getParaForStringOrNull("provinceId") == "" ? null : pd.getInt("provinceId");
        Integer cityId = pd.getParaForStringOrNull("cityId") == "" ? null : pd.getInt("cityId");
        Integer areaId = pd.getParaForStringOrNull("areaId") == "" ? null : pd.getInt("areaId");
        String address = pd.getParaForStringOrNull("address") == "" ? null : pd.getParaForString("address");

        AsUserInfo userinfo = new AsUserInfo();
        userinfo.setUserInfoId(user.getUserInfoId());
        userinfo.setNickName(pd.getParaForString("nickName"));
        userinfo.setSex(pd.getInt("sex"));
        //touxiang
        String msg = CheckFile("img4");
        operaFlag = msg == "" || "".equals(msg) ? true : false;
        ValidatorBreakException.NOT_TRUE(operaFlag, msg);

        uploadModule1(UploadPathEnum.USERS, 4);
        String avata = (pd.get("img4") == null || "".equals(pd.getString("img0"))) ? user.getAccount() : pd.get("img4").toString();
        userinfo.setBirthday(pd.getString("birthday"));
        userinfo.setProvinceId(provinceId);
        userinfo.setCityId(cityId);
        userinfo.setAreaId(areaId);
        userinfo.setAddress(address);
        userinfo.setAvata(avata);
        operaFlag = userInfoMapper.updateByPrimaryKeySelective(userinfo) == 1;
        ValidatorBreakException.NOT_TRUE(operaFlag);
        return renderSuccess();
    }


    /**
     * ########################################商户背景图操作#############################################
     */

    /**
     * 通过商户id 获取背景图
     *
     * @param shopId
     * @return
     */
    public List<AsShopPic> getShopPicImgs(int shopId) {
        return shopPicMapper.selectByShopId(shopId);
    }

    /**
     * 批量保存商户背景图片
     *
     * @param files 图片文件数组
     * @return
     * @author tgb
     * @version 1.0
     */
    public int saveShopPicImgs(MultipartFile[] files, int shopId) {
        for (int j = 0; j < files.length; j++) {
            if (!StringUtils.isEmpty(files[j].getOriginalFilename())) {
                try {
                    imgUpload(shopId, files[j].getBytes());
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return 0;
    }

    /**
     * 修改商品图片
     * @param files
     * @return
     */
    public int editShopPicImgs(MultipartFile files, int shopId,
                             int shopPicId) {

        String ftpFile = UuidUtil.get32UUID() + ".jpg";
        String ftpDir = Const.SHOPMAINPICFILEPATHIMG;
        String url = ftpDir + "/" + ftpFile;
        boolean flag = false;
        try {
            // ftp上传背景图片
            flag = ftpUpload(files.getBytes(), ftpFile, ftpDir);
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (flag) {
            AsShopPic asShopPic = new AsShopPic();
             asShopPic.setShopId(shopId);
            asShopPic.setShopPicId(shopPicId);
            asShopPic.setPicUrl(url);
            shopPicMapper.updateByPrimaryKeySelective(asShopPic);

        }
        return 1;
    }
    /**
     * 删除图片
     *
     * @param shopPicId
     * @return
     * @author tgb
     * @version 1.0
     */
    public Object deleteShopPicImg(int shopPicId) {
        return shopPicMapper.deleteByPrimaryKey(shopPicId);
    }

    /**
     * 批量删除 图片
     *
     * @param ids
     * @author tgb
     * @version 1.0
     */
    public int deleteShopPicImgAlls(int[] ids) {
        return shopPicMapper.deleteShopPicImgAlls(ids);
    }

    /**
     * 上传商户背景图字节流方法
     *
     * @param id
     * @param bytes
     * @author tgb
     * @version 1.0
     */
    private void imgUpload(int id, byte[] bytes) {
        String ftpFile = UuidUtil.get32UUID() + ".jpg";
        String ftpDir = Const.SHOPMAINPICFILEPATHIMG;
        // ftp上传商品图片
        boolean flag = ftpUpload(bytes, ftpFile, ftpDir);
        if (flag) {
            AsShopPic pic = new AsShopPic();
            pic.setShopId(id);
            pic.setPicUrl(ftpDir + "/" + ftpFile);
            shopPicMapper.insertSelective(pic);
        }
    }

    /**
     * 上传ftp
     *
     * @param bytes   --文件
     * @param ftpFile --图片地址
     * @param ftpDir  --ftp路径
     * @return
     */
    private boolean ftpUpload(byte[] bytes, String ftpFile, String ftpDir) {
        String path = Const.FILEPATHIMGDIR + ftpDir;
        // byte[] bytes = Base64.decode(goodsImg);
        FtpConManager ftpConManager = FtpConManager.getInstance();
        try {
            ftpConManager.login(PropertyUtils.getFTP_URL(),
                    PropertyUtils.getFTP_USERNAME(),
                    PropertyUtils.getFTP_PASS());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return ftpConManager.uploadFileByInputStream(new ByteArrayInputStream(
                bytes), path, ftpFile);
    }

    /**
     * 获取商户管理员信息
     * @param userId
     * @return
     */
    public Object  getShopAccountOwnerInfo(Integer userId){
        return asShopOwnerInfoMapper.getOwnerInfo(userId);
    }

}