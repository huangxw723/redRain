package com.aoshi.service.system;

import java.io.ByteArrayInputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import net.sf.json.JSONArray;

import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.session.Session;
import org.apache.shiro.subject.Subject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.aoshi.common.asenum.DictParam;
import com.aoshi.common.asenum.UploadPathEnum;
import com.aoshi.common.asenum.UserTypeEnum;
import com.aoshi.controller.base.BaseController;
import com.aoshi.dao.AsAdminInfoMapper;
import com.aoshi.dao.AsAreaMapper;
import com.aoshi.dao.AsBusinessDistrictMapper;
import com.aoshi.dao.AsCityMapper;
import com.aoshi.dao.AsDictParamMapper;
import com.aoshi.dao.AsProvinceMapper;
import com.aoshi.dao.AsShopMapper;
import com.aoshi.dao.AsShopOwnerInfoMapper;
import com.aoshi.dao.AsShopPicMapper;
import com.aoshi.dao.AsShopSpellMapper;
import com.aoshi.dao.AsShopStaffInfoMapper;
import com.aoshi.dao.AsStoreMapper;
import com.aoshi.dao.AsUserMapper;
import com.aoshi.dao.AsWalletMapper;
import com.aoshi.dao.AsYpBaseCategoryMapper;
import com.aoshi.dao.AsYpSysMessageMapper;
import com.aoshi.dao.DaoSupport;
import com.aoshi.dao.SysRoleMapper;
import com.aoshi.dao.SysUrRelationMapper;
import com.aoshi.dao.SysUserMapper;
import com.aoshi.domain.AsAdminInfo;
import com.aoshi.domain.AsArea;
import com.aoshi.domain.AsBusinessDistrict;
import com.aoshi.domain.AsCity;
import com.aoshi.domain.AsProvince;
import com.aoshi.domain.AsShop;
import com.aoshi.domain.AsShopOwnerInfo;
import com.aoshi.domain.AsShopPic;
import com.aoshi.domain.AsShopSpell;
import com.aoshi.domain.AsShopStaffInfo;
import com.aoshi.domain.AsStore;
import com.aoshi.domain.AsUser;
import com.aoshi.domain.AsWallet;
import com.aoshi.domain.AsYpBaseCategory;
import com.aoshi.domain.SysRole;
import com.aoshi.domain.SysUrRelation;
import com.aoshi.domain.SysUser;
import com.aoshi.entity.Page;
import com.aoshi.entity.R;
import com.aoshi.entity.system.Menu;
import com.aoshi.entity.system.Role;
import com.aoshi.entity.system.User;
import com.aoshi.resolver.ValidatorBreakException;
import com.aoshi.service.base.AsBaseService;
import com.aoshi.util.ArithmeticUtil;
import com.aoshi.util.CommonUtils;
import com.aoshi.util.Conditions;
import com.aoshi.util.Const;
import com.aoshi.util.DateUtil;
import com.aoshi.util.FtpConManager;
import com.aoshi.util.PageData;
import com.aoshi.util.PinyinUtil;
import com.aoshi.util.RightsHelper;
import com.aoshi.util.SecurityUtil;
import com.aoshi.util.SmsUtils;
import com.aoshi.util.TokenManager;
import com.aoshi.util.Tools;
import com.aoshi.util.UuidUtil;
import com.mysql.jdbc.util.Base64Decoder;

@Service
public class UserService extends AsBaseService {

	private final static String PAGE_PATH = "system/admin/";
	@Autowired
	private DaoSupport dao;
	@Autowired
	private MenuService menuService;
	@Autowired
	private RoleService roleService;
	@Autowired
	private AsUserMapper asUserMapper;
	@Autowired
	private AsShopOwnerInfoMapper asShopOwnerInfoMapper;
	@Autowired
	private AsShopStaffInfoMapper asShopStaffInfoMapper;
	@Autowired
	private SysUserMapper sysUserMapper;
	@Autowired
	private AsYpSysMessageMapper asYpSysMessageMapper;
	@Autowired
	private AsAdminInfoMapper asAdminInfoMapper;
	@Autowired
	SysUrRelationMapper sysUrRelationMapper;
	@Autowired
	SysRoleMapper sysRoleMapper;
	@Autowired
	private AsShopMapper asShopMapper;
	@Autowired
	private AsDictParamMapper asDictParamMapper;
	@Autowired
    private AsWalletMapper asWalletMapper;
    @Autowired
    private AsStoreMapper asStoreMapperr;
    @Autowired
    private AsYpBaseCategoryMapper ypBaseCategoryMapper;
    @Autowired
    private AsShopSpellMapper asShopSpellMapper;
    @Autowired
	SmsUtils smsUtils;
    @Autowired
    private AsProvinceMapper asProvinceMapper;
    @Autowired
    private AsCityMapper asCityMapper;
    @Autowired
    private AsAreaMapper asAreaMapper;
    @Autowired
    private AsBusinessDistrictMapper asBusinessDistrictMapper;
    @Autowired
    private AsStoreMapper asStoreMapper;
    @Autowired
    private AsShopPicMapper  asShopPicMapper;
	/*
	 * 通过id获取数据
	 */
	public PageData findByUiId(PageData pd) throws Exception {
		return (PageData) dao.findForObject("UserXMapper.findByUiId", pd);
	}

	/*
	 * 通过loginname获取数据
	 */
	public PageData findByUId(Object pd) throws Exception {
		return (PageData) dao.findForObject("UserXMapper.findByUId", pd);
	}

	/*
	 * 通过邮箱获取数据
	 */
	public PageData findByUE(PageData pd) throws Exception {
		return (PageData) dao.findForObject("UserXMapper.findByUE", pd);
	}

	/*
	 * 通过编号获取数据
	 */
	public PageData findByUN(PageData pd) throws Exception {
		return (PageData) dao.findForObject("UserXMapper.findByUN", pd);
	}

	/*
	 * 保存用户
	 */
	public void saveU(PageData pd) throws Exception {
		dao.save("UserXMapper.saveU", pd);
	}

	/*
	 * 修改用户
	 */
	public void editU(PageData pd) throws Exception {
		dao.update("UserXMapper.editU", pd);
	}

	/*
	 * 换皮肤
	 */
	public void setSKIN(PageData pd) throws Exception {
		dao.update("UserXMapper.setSKIN", pd);
	}

	/*
	 * 删除用户
	 */
	public void deleteU(PageData pd) throws Exception {
		dao.delete("UserXMapper.deleteU", pd);
	}

	/*
	 * 批量删除用户
	 */
	public void deleteAllU(String[] USER_IDS) throws Exception {
		dao.delete("UserXMapper.deleteAllU", USER_IDS);
	}

	/*
	 * 用户列表(用户组)
	 */
	@SuppressWarnings("unchecked")
	public List<PageData> listPdPageUser(Page page) throws Exception {
		return (List<PageData>) dao.findForList("UserXMapper.userlistPage",
				page);
	}

	/*
	 * 用户列表(全部)
	 */
	@SuppressWarnings("unchecked")
	public List<PageData> listAllUser(PageData pd) throws Exception {
		return (List<PageData>) dao.findForList("UserXMapper.listAllUser", pd);
	}

	/*
	 * 用户列表(供应商用户)
	 */
	@SuppressWarnings("unchecked")
	public List<PageData> listGPdPageUser(Page page) throws Exception {
		return (List<PageData>) dao.findForList("UserXMapper.userGlistPage",
				page);
	}

	/*
	 * 保存用户IP
	 */
	public void saveIP(PageData pd) throws Exception {
		dao.update("UserXMapper.saveIP", pd);
	}

	/*
	 * 登录判断
	 */
	public PageData getUserByNameAndPwd(PageData pd) {
		return (PageData) dao.findForObject("UserXMapper.getUserInfo", pd);
	}

	/*
	 * 跟新登录时间
	 */
	public void updateLastLogin(PageData pd) {
		dao.update("UserXMapper.updateLastLogin", pd);
	}

	/*
	 * 通过id获取数据
	 */
	public User getUserAndRoleById(String USER_ID) {
		return (User) dao.findForObject("UserMapper.getUserAndRoleById",
				USER_ID);
	}

	/*
	 * 获取用户选择列表
	 */
	@SuppressWarnings("unchecked")
	public List<PageData> listAll() throws Exception {
		return (List<PageData>) dao.findForList("UserMapper.listAll", null);
	}

	/**
	 * 登录
	 * 
	 * @author yangyanchao
	 * @date 2016年10月19日
	 * @param c
	 * @return
	 */
	public Object login(BaseController c) {

		initialized(c);
		boolean authorizedFlag  = false;
		Integer auditStatus=1010;
		String auditText="";
		Subject currentUser = SecurityUtils.getSubject();
		Session session = currentUser.getSession();
	    
		String account = pd.getParaForString("account");
		String password = pd.getParaForString("password");
		String SHA1Password = CommonUtils.encryptPwd(password, 2);
		
		
       if(StringUtils.isEmpty(session.getAttribute(Const.IS_BACK_LOGIN))){
        String code = pd.getParaForString("code");
		// 检查验证码
		
		String sessionCode = (String) session
				.getAttribute(Const.SESSION_SECURITY_CODE);
		ValidatorBreakException
        .NOT_TRUE(code.equalsIgnoreCase(sessionCode),
				"验证码不正确或已过期");
		session.removeAttribute(Const.SESSION_SECURITY_CODE);
        }
		// 检查帐号密码
		Conditions accountAndPwd = Conditions.newInstance();
		accountAndPwd.put("account", account);
		accountAndPwd.put("password", SHA1Password);
		SysUser sysUser = (SysUser) sysUserMapper
				.findByAccountAndPwd(accountAndPwd);
		ValidatorBreakException.NOT_NULL(sysUser, "帐号或密码错误");

		// 禁用&删除 状态判断
		String USER_ID = sysUser.getUserId();
		Conditions asUser = (Conditions) asUserMapper.findBySysUserId(USER_ID);
		if (asUser == null) {
			asUser = new Conditions();
			asUser.putData("password", CommonUtils.encryptPwd(password))
					.putData("account", account)
					.putData("createTime", CommonUtils.getCurDate())
					.putData("type", UserTypeEnum.ADMIN.getId())
					.putData("sysUserId", USER_ID);
			operaFlag = asUserMapper.insertSelective(asUser) == 1;
			ValidatorBreakException.NOT_TRUE(operaFlag, "帐号初始化错误");
		}

		int status = asUser.getInt("status");
		ValidatorBreakException.NOT_TRUE(status != 1, "帐号或密码不正确");

		int userId = asUser.getInt("userId");
		if (UserTypeEnum.STAFF.getId() == asUser.getInt("type")) {

			Conditions asShopStaff = (Conditions) asShopStaffInfoMapper
					.findByUserId(userId);
			ValidatorBreakException.NOT_NULL(asShopStaff);

			status = asShopStaff.getInt("status");
			ValidatorBreakException.NOT_TRUE(status != 1, "帐号已被禁用");
		} else if (UserTypeEnum.SELLER.getId() == asUser.getInt("type")) {

			Conditions asShopOwner = asShopOwnerInfoMapper.findByUserId(userId);
			ValidatorBreakException.NOT_NULL(asShopOwner);

			status = asShopOwner.getInt("status");
			ValidatorBreakException.NOT_TRUE(status != 1, "帐号已被禁用");
			
			if(StringUtils.isEmpty(asShopOwner.get("auditStatus"))){
				 auditStatus = -1;
			}
			if(asShopOwner.get("auditStatus")!=null){
				 auditStatus = asShopOwner.getInt("auditStatus");
				 auditText = asShopOwner.get("reason")==null?"":asShopOwner.get("reason").toString();
			}
			
			if(!Arrays.asList(Const.MERCHANT_LOGIN).contains(auditStatus)){
				ValidatorBreakException.NOT_TRUE(false, "帐号异常！");
			}
			
		} else if (UserTypeEnum.ADMIN.getId() == asUser.getInt("type")||
				UserTypeEnum.OPERATIONMANAGER.getId() == asUser.getInt("type")) {

			AsAdminInfo adminInfo = (AsAdminInfo) asAdminInfoMapper
					.findByUserId(userId);
			ValidatorBreakException.NOT_NULL(adminInfo);

			status = adminInfo.getStatus();
			ValidatorBreakException.NOT_TRUE(status != 1, "帐号已被禁用");
		}
		int merChanType = 0;
		if (UserTypeEnum.SELLER.getId() == asUser.getInt("type")
				||UserTypeEnum.STAFF.getId() == asUser.getInt("type")) {
			merChanType = asShopMapper.selectByPrimaryKey(String.valueOf(sysUser.getAsShopId())).getType();
		}
		User user = new User();
		user.setUSER_ID(sysUser.getUserId());
		user.setUSERNAME(sysUser.getUsername());
		user.setPASSWORD(sysUser.getPassword());
		user.setNAME(sysUser.getName());
		user.setRIGHTS(sysUser.getRights());
		user.setROLE_ID(sysUser.getRoleId());
		user.setSTATUS(sysUser.getStatus());
		user.setAs_shop_id(String.valueOf(sysUser.getAsShopId()));
		user.setAs_user_id(String.valueOf(sysUser.getAsUserId()));
		user.setNewUserId(asUser.get("userId").toString());
		session.setAttribute(Const.SESSION_USER, user);
		 
		// shiro加入身份验证
		Subject subject = SecurityUtils.getSubject();
		subject.getSession().setTimeout(-1000l);
		UsernamePasswordToken token = new UsernamePasswordToken(account,
				password);
		subject.login(token);

		session.setAttribute("SYSNAME", Tools.readTxtFile(Const.SYSNAME));
		//状态说明  1003, 1004, 1005, 1006, 1007 依次为==》 待提交、未审、 业主已审、 管理员已审、驳回
		
		switch (auditStatus) {
		 case -1:
			 return renderSuccess(3,new R(1003, merChanType, null));
		 case 0:
			 return renderSuccess(3,new R(1004, merChanType, null));
		 case 1:
			 return renderSuccess(3,new R(1005, merChanType, null));
		 case 2:
			 return renderSuccess(3,new R(1006, merChanType, null));
		 case 3:
			 return renderSuccess(3,new R(1007, merChanType, auditText));
		 case 1010:
			 return renderSuccess();
		 
		}
		 return renderSuccess();
		
	}

	/**
	 * 后台主页面
	 * 
	 * @author yangyanchao
	 * @date 2016年10月19日
	 * @param c
	 * @return
	 */
	public Object homePage(BaseController c, String changeMenu) {

		initialized(c);

		// shiro管理的session
		Subject currentUser = SecurityUtils.getSubject();
		Session session = currentUser.getSession();

		User user = (User) session.getAttribute(Const.SESSION_USER);
		if (user == null) {
			return renderView(PAGE_PATH + "login");
		}

		// 查询用户权限信息
		User userr = (User) session.getAttribute(Const.SESSION_USERROL);
		if (null == userr) {
			user = getUserAndRoleById(user.getUSER_ID());
			session.setAttribute(Const.SESSION_USERROL, user);
		} else {
			user = userr;
		}

		Role role = user.getRole();
		//String roleRights = role != null ? role.getRIGHTS() : "";
		
		//用户与角色的关系
		List<String> rights = new ArrayList<>();
		if(session.getAttribute(Const.SESSION_ROLE_RIGHTS) == null){
			List<SysUrRelation> relations = sysUrRelationMapper.selectByUserId(user.getUSER_ID());
			List<String> roleIds = new ArrayList<>();
			for(int i=0;i<relations.size();i++){
				roleIds.add(relations.get(i).getRoleId());
			}
			if(roleIds.size() > 0){
				List<SysRole> roles = sysRoleMapper.seletByRoleIds(roleIds);
				//用户拥有多个角色rights
				for(int i=0;i<roles.size();i++){
					rights.add(roles.get(i).getRights());
				}
			}
			
			
		}else{
			String str = (String) session.getAttribute(Const.SESSION_ROLE_RIGHTS);
			String[] array = str.split(";");
			for(String s:array){
				rights.add("s");
			}
		}
		//历史遗留问题，原先的角色和用户是一对一关系
		if(!StringUtils.isEmpty(role.getRIGHTS())){
			rights.add(role.getRIGHTS());
		}
		
		// 避免每次拦截用户操作时查询数据库，以下将用户所属角色权限、用户权限限都存入session
		if(rights.size() > 0){
			StringBuffer sb=new StringBuffer();
			for(String item:rights){
				sb.append(item);
				sb.append(";");
			}
			session.setAttribute(Const.SESSION_ROLE_RIGHTS, sb.toString()); // 将角色权限存入session
		}
		
		session.setAttribute(Const.SESSION_USERNAME, user.getUSERNAME()); // 放入用户名

		List<Menu> allmenuList = new ArrayList<Menu>();

		if (null == session.getAttribute(Const.SESSION_allmenuList)) {
			allmenuList = menuService.listAllMenu();
			/*if (Tools.notEmpty(roleRights)) {
				for (Menu menu : allmenuList) {
					menu.setHasMenu(RightsHelper.testRights(roleRights,
							menu.getMENU_ID()));
					if (menu.isHasMenu()) {
						List<Menu> subMenuList = menu.getSubMenu();
						for (Menu sub : subMenuList) {
							sub.setHasMenu(RightsHelper.testRights(roleRights,
									sub.getMENU_ID()));
						}
					}
				}
			}*/
			for (Menu menu : allmenuList) {
				menu.setHasMenu(RightsHelper.testMultiRights(rights,menu.getMENU_ID()));
				if (menu.isHasMenu()) {
					List<Menu> subMenuList = menu.getSubMenu();
					for (Menu sub : subMenuList) {
						sub.setHasMenu(RightsHelper.testMultiRights(rights,
								sub.getMENU_ID()));
					}
				}
			}
			
			session.setAttribute(Const.SESSION_allmenuList, allmenuList); // 菜单权限放入session中
		} else {
			allmenuList = (List<Menu>) session
					.getAttribute(Const.SESSION_allmenuList);
		}

		// 切换菜单=====
		List<Menu> menuList = new ArrayList<Menu>();
		if (null == session.getAttribute(Const.SESSION_menuList)
				|| ("yes".equals(changeMenu))) {
			List<Menu> menuList1 = new ArrayList<Menu>();
			List<Menu> menuList2 = new ArrayList<Menu>();

			// 拆分菜单
			for (int i = 0; i < allmenuList.size(); i++) {
				Menu menu = allmenuList.get(i);
				if ("1".equals(menu.getMENU_TYPE())) {
					menuList1.add(menu);
				} else {
					menuList2.add(menu);
				}
			}

			session.removeAttribute(Const.SESSION_menuList);
			if ("2".equals(session.getAttribute("changeMenu"))) {
				session.setAttribute(Const.SESSION_menuList, menuList1);
				session.removeAttribute("changeMenu");
				session.setAttribute("changeMenu", "1");
				menuList = menuList1;
			} else {
				session.setAttribute(Const.SESSION_menuList, menuList2);
				session.removeAttribute("changeMenu");
				session.setAttribute("changeMenu", "2");
				menuList = menuList2;
			}
		} else {
			menuList = (List<Menu>) session
					.getAttribute(Const.SESSION_menuList);
		}

		if (null == session.getAttribute(Const.SESSION_QX)) {
			session.setAttribute(Const.SESSION_QX, this.getUQX(session)); // 按钮权限放到session中
		}

		addViewData("user", user);
		addViewData("menuList", menuList);
		addViewData("pd", pd);

		// 未读消息提醒
		String userId = getUser().getNewUserId();
		int systemCounts = asYpSysMessageMapper
				.countUnreadSystemMessage(userId);
		int personalCounts = asYpSysMessageMapper
				.countUnreadPersonalMessage(userId);
		session.setAttribute("unreadMessageCounts",
				(int) ArithmeticUtil.ADD(systemCounts, personalCounts));

		// 获取 未读 系统消息&个人消息
		List<Object> msgList = asYpSysMessageMapper
				.getAllUnreadMessageList(userId);
		if (msgList.size() > 5) {
			msgList = msgList.subList(0, 5);
		}

		addViewData("unreadMessageList", msgList);

		return renderView(PAGE_PATH + "index");
	}

	/**
	 * 获取用户权限
	 * 
	 * @author yangyanchao
	 * @date 2016年10月19日
	 * @param session
	 * @return
	 */
	private Map<String, String> getUQX(Session session) {
		PageData pd = new PageData();
		Map<String, String> map = new HashMap<String, String>();
		try {
			String USERNAME = session.getAttribute(Const.SESSION_USERNAME)
					.toString();
			pd.put(Const.SESSION_USERNAME, USERNAME);
			String ROLE_ID = findByUId(session.getAttribute(Const.SESSION_USER)).get("ROLE_ID").toString();

			pd.put("ROLE_ID", ROLE_ID);

			PageData pd2 = new PageData();
			pd2.put(Const.SESSION_USERNAME, USERNAME);
			pd2.put("ROLE_ID", ROLE_ID);

			pd = roleService.findObjectById(pd);

			pd2 = roleService.findGLbyrid(pd2);
			if (null != pd2) {
				map.put("FX_QX", pd2.get("FX_QX").toString());
				map.put("FW_QX", pd2.get("FW_QX").toString());
				map.put("QX1", pd2.get("QX1").toString());
				map.put("QX2", pd2.get("QX2").toString());
				map.put("QX3", pd2.get("QX3").toString());
				map.put("QX4", pd2.get("QX4").toString());
			}

			map.put("adds", pd.getString("ADD_QX"));
			map.put("dels", pd.getString("DEL_QX"));
			map.put("edits", pd.getString("EDIT_QX"));
			map.put("chas", pd.getString("CHA_QX"));
		} catch (Exception e) {
			logger.error(e.toString(), e);
		}
		return map;
	}

	public Object findByAccountDetail(String account) {
		return dao.findForObject("UserMapper.selectUserInfoByAccount",
				account);
	}

	public List<User> getUserByRoleId(String roleId) {
		return (List<User>) dao.findForList("UserMapper.getUserByRoleId", roleId);
	}
	/**
	 * 获取admin手机号
	 * @param phone
	 * @author wangjs
	 * @date 2017年02月20日
	 * @return
	 */
	public Object getAdminPhone(BaseController c){
		// shiro管理的session
		initialized(c);

		Subject currentUser = SecurityUtils.getSubject();
		Session session = currentUser.getSession();

		User user = (User) session.getAttribute(Const.SESSION_USER);
		String userId = user.getNewUserId();

		AsAdminInfo adminInfo = (AsAdminInfo) asAdminInfoMapper
				.findByUserId(userId);

		String phone = adminInfo.getAdminPhone();
		String adminPrKey = adminInfo.getAdminInfoId().toString();
		addViewData("adminInfoId", adminPrKey);
		addViewData("userId", userId);
		addViewData("adminPhone", phone);
		return renderView(PAGE_PATH + "adminphone");
	}


	/**
	 * 提现验证获获取admin手机号
	 * @param phone
	 * @author wangjs
	 * @date 2017年02月20日
	 * @return
	 */
	public Object getSendAdminPhone(BaseController c){
		// shiro管理的session
		initialized(c);

		Subject currentUser = SecurityUtils.getSubject();
		Session session = currentUser.getSession();
			String capitalId = pd.getString("capitalId");
			String status = pd.getString("status");


			User user = (User) session.getAttribute(Const.SESSION_USER);
			String userId = user.getNewUserId();

			AsAdminInfo adminInfo = (AsAdminInfo) asAdminInfoMapper
					.findByUserId(userId);

			String phone = adminInfo.getAdminPhone();
			String adminPrKey = adminInfo.getAdminInfoId().toString();

			addViewData("adminInfoId", adminPrKey);
			addViewData("capitalId",capitalId);
			addViewData("status", status);

			addViewData("userId", userId);
			addViewData("adminPhone", phone);
			return renderView("manage/common/checkphone");



	}

	/**
	 * 修改admin手机号
	 * @param phone
	 * @param code
	 * @param adminInfoId
	 * @author wangjs
	 * @date 2017年02月20日
	 * @return
	 */
	public Object updateAdminPhone(BaseController c){
		// shiro管理的session
		initialized(c);
		String phone = pd.getString("phone");
		String code = pd.getString("adminCode");
		String adminInfoId = pd.getString("adminInfoId");
		Map<String,Object> data = new HashMap<String,Object>();
		Subject currentUser = SecurityUtils.getSubject();
		Session session = currentUser.getSession();
		String adminCode =	session.getAttribute("adminCode")==null ? "" : session.getAttribute("adminCode").toString();
		String adminPhone =	session.getAttribute("adminPhone")==null ? "" : session.getAttribute("adminPhone").toString();
		data.put("phone",phone);
		if (code.equals(adminCode)&&adminPhone.equals(phone)) {
			AsAdminInfo asAdminInfo = new AsAdminInfo();
			asAdminInfo.setAdminInfoId(Integer.valueOf(adminInfoId));
			asAdminInfo.setAdminPhone(adminPhone);
			 operaFlag = asAdminInfoMapper.updateByPrimaryKeySelective(asAdminInfo)==1;
			ValidatorBreakException.NOT_TRUE(operaFlag, "修改失败");
			session.setAttribute("checkFlag","");
			return renderSuccess();
		} 
		else {
			//data.put("messge","验证码已过期");
			ValidatorBreakException.NOT_TRUE(false, "验证码已过期");
		}
		return renderSuccess();
	}
	
	/**
	 * @describe 发送验证码
	 * 
	 * @param phone 手机号码
	 * @param userType 用户注册类型 1商场  2 店铺
	 * @param type 1注册，2代表找回密码
	 * @return
	 */
	public Object getVerifyCode(String account, Integer userType, String phone, Integer type, String password,BaseController c) {
		initialized(c);
		if(userType==1){
		checkPhone(phone);
		int code = new Random().nextInt(999999) + 1;
		//账号检查
		 operaFlag = asUserMapper.isAccountExists(Conditions.newInstance().putData("phone", phone)) > 0;
         ValidatorBreakException.NOT_TRUE(!operaFlag, "账号已被注册！");
		//手机号检查 
         operaFlag = asUserMapper.isAccountExists(Conditions.newInstance().putData("account", account)) > 0;
         ValidatorBreakException.NOT_TRUE(!operaFlag, "手机号已注册！");

		String msg = "【澳视官网】提醒您，您的验证码为" + code + "，为保证您的权益，请保管好该验证码。此验证码有效期60秒";
		smsUtils.sendSms(phone, msg);
		//用户信息token
		String token = new TokenManager().createToken();
		Map<String, Object> hashMap = new HashMap<>();
		hashMap.put("verifyCode", code);
		hashMap.put("token", token);
		Map<String, Object> tokenMap = new HashMap<>();
		tokenMap.put("account", account);
		tokenMap.put("phone", phone);
		tokenMap.put("password", password);
		tokenMap.put("code", code);
		tokenMap.put("userType", userType);
		tokenMap.put("type", type);
		tokenMap.put("time", DateUtil.getTime());
		TokenManager.setToken(token, tokenMap);
		addViewData("token", token);
		addViewData("verifyCode", code);
		addViewData("phone", phone);
		addViewData("userType", userType);
		return renderSuccess(0,new R(0, "操作成功！", hashMap));
		}else{
			return renderSuccess(0,new R(0, "操作成功！", null));
		}
	}
	
	/**
	 * 校验手机号码
	 * 
	 * @param phone
	 *            手机号码
	 */
	public void checkPhone(String phone) {
		String phonePattern = "^[1][0-9]{10}$";
		Pattern pattern = Pattern.compile(phonePattern);
		Matcher matcher = pattern.matcher(phone);
		if (!matcher.matches())
			throw new RuntimeException("手机号码不正确");
	}


	/**
	 * @describe 注册
	 * 
	 */
	public Object register(Integer userType, String account, String password,
			String phone, BaseController c) {
		initialized(c);
		//手机号 验证码 账号类型 检查
		if (StringUtils.isEmpty(userType) || StringUtils.isEmpty(account) || StringUtils.isEmpty(password) || StringUtils.isEmpty(phone)
				) {
			 ValidatorBreakException.NOT_TRUE(!operaFlag, "参数错误！");
		}
				
		//账号检查
		 operaFlag = asUserMapper.isAccountExistsCheck(Conditions.newInstance().putData("type", UserTypeEnum.STAFF.getId()).putData("account", account)) > 0;
        ValidatorBreakException.NOT_TRUE(!operaFlag, "账号已被注册！");
        //账号检查
        operaFlag = asUserMapper.isAccountExistsCheck(Conditions.newInstance().putData("type", UserTypeEnum.SELLER.getId()).putData("account", account)) > 0;
        ValidatorBreakException.NOT_TRUE(!operaFlag, "账号已被注册！");
        operaFlag = asUserMapper.isAccountExistsCheck(Conditions.newInstance().putData("type", UserTypeEnum.STAFF.getId()).putData("phone", phone)) > 0;
        //手机号检查
        ValidatorBreakException.NOT_TRUE(!operaFlag, "手机号已被注册！");
        //手机号号检查
        operaFlag = asUserMapper.isAccountExistsCheck(Conditions.newInstance().putData("type", UserTypeEnum.SELLER.getId()).putData("phone", phone)) > 0;
        ValidatorBreakException.NOT_TRUE(!operaFlag, "手机号已被注册！");
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
      //用户信息token
      		String token = new TokenManager().createToken();
      		Map<String, Object> hashMap = new HashMap<>();
      		hashMap.put("userId", UserTypeEnum.SELLER.getId());
      		hashMap.put("token", String.valueOf(user.getInt("userId")));
      		Map<String, Object> tokenMap = new HashMap<>();
      		tokenMap.put("userId", user.getInt("userId"));
      		tokenMap.put("shopId", cd.getInt("shopId"));
      		tokenMap.put("time", DateUtil.getTime());
      		TokenManager.setToken(token, tokenMap);
        
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
        Subject currentUser = SecurityUtils.getSubject();
		Session session = currentUser.getSession();
		session.setAttribute(Const.IS_BACK_LOGIN, "true");
        req.setAttribute("account", account);
        req.setAttribute("password", password);
        this.login(c);
        return renderSuccess(0,new R(0, "操作成功！", hashMap));
	}

	/**
	 * @describe 商场/店铺用户 资料页面    
	 * 
	 *            
	 */
	public Object operMerchantUserInfoPage(String id,BaseController c) {
		initialized(c);
		String userId=  SecurityUtil.decodeAES(id);
		
		AsShopOwnerInfo shopOwnerInfo =(AsShopOwnerInfo) asShopOwnerInfoMapper.getShopOwnerByUserId(userId);

		return renderSuccess(0,new R(0, "操作成功！", shopOwnerInfo));
	}
	
	/**
	 * @describe 商场/店铺用户 资料 保存   
	 * 
	 * 申请人姓名  证件类型 证件号码  法人姓名  证件类型 证件号码  证件照（2） 身份证照  营业执照  经营许可证
	 * @param applicant
     * @param applicantCertificateType
     * @param applicantCertificateNum
     * @param corporation 
     * @param corporationCertificateType
     * @param corporationCertificateNum
     * @param identityFront
     * @param identityBack
     * @param businessLicence
     * @param businessCertificate
	 * @throws Exception 
	 *            
	 */
	public Object operMerchantUserInfo(String applicant,
			Integer applicantCertificateType,
			String applicantCertificateNum, 
			String corporation,
			Integer corporationCertificateType,
			String corporationCertificateNum, 
			String identityFront,
			String identityBack, 
			String businessLicence,
			String businessCertificate,
			String token,
			String otype
			) throws Exception {
		initialized(c);
		AsUser user=null;
		if(!StringUtils.isEmpty(otype)){
			if(otype.equals("2")){
				String userId=  SecurityUtil.decodeAES(token.toString());
				user = (AsUser) asUserMapper.selectByPrimaryKey(userId);
			}
		}
		else if(!StringUtils.isEmpty(token)){
			user = (AsUser) asUserMapper.selectByPrimaryKey(token);
			if (user == null)
				throw new Exception("系统繁忙");
		}
		AsShopOwnerInfo shopOwnerInfo = (AsShopOwnerInfo)asShopOwnerInfoMapper.getShopOwnerByUserId(String.valueOf(user.getUserId()));
		//创建商户店铺管理员信息
        Conditions shopOwner = Conditions.newInstance();
        shopOwner.putData("shopOwnerInfoId", shopOwnerInfo.getShopOwnerInfoId())
        .putData("userId", user.getUserId())
        .putData("shopId", shopOwnerInfo.getShopId())
        .putData("applicant", applicant)
        .putData("applicantCertificateType", applicantCertificateType)
        .putData("applicantCertificateNum", applicantCertificateNum)
        .putData("corporation", corporation)
        .putData("corporationCertificateType", corporationCertificateType)
        .putData("corporationCertificateNum", corporationCertificateNum)
        .putData("identityBack", uploadModule(identityBack,UploadPathEnum.USERS))
        .putData("identityFront", uploadModule(identityFront,UploadPathEnum.USERS))
        .putData("businessLicence", uploadModule(businessLicence,UploadPathEnum.USERS))
        .putData("businessCertificate", uploadModule(businessCertificate,UploadPathEnum.USERS));
                  
        operaFlag = asShopOwnerInfoMapper.updateByPrimaryKeySelective(shopOwner) == 1;
        ValidatorBreakException.NOT_TRUE(operaFlag);
		return renderSuccess(0,new R(0, "操作成功！", user.getUserId()));
	}
	

	 /**
     * 多字段
     *
     * @author yangyanchao
     * @date 2017-03-02
     * @param uploadPathEnum 路径
     * @param colNames 数据库对应字段名
     */
	private String uploadModule(String imgType, UploadPathEnum uploadPathEnum,
			String... colNames) {
		String fileLink = null;

			if (!StringUtils.isEmpty(imgType)) {
				byte[] bytes = Base64Decoder.decode(imgType.getBytes(), 0,
						imgType.getBytes().length);
				String ftpDir = uploadPathEnum.getRootPath()
						+ uploadPathEnum.getPath();
				String ftpFile = c.get32UUID() + ".jpg";
				
				FtpConManager ftpConManager = FtpConManager.getInstance();
				try {

                    Properties props = new Properties();
                    props.load(new ClassPathResource("dbconfig.properties").getInputStream());
					ftpConManager.login(props.getProperty("FTP_URL"),
                            props.getProperty("FTP_USERNAME"),
                            props.getProperty("FTP_PASS"));

					boolean flag = ftpConManager.uploadFileByInputStream(new ByteArrayInputStream(bytes),
							ftpDir, ftpFile);
					if (flag) {
						fileLink = uploadPathEnum.getPath() +"/"+ ftpFile;
					}else{
						ValidatorBreakException.NOT_TRUE(!operaFlag, "上传文件失败！");
					}
				} catch (Exception e) {
					e.printStackTrace();
				} finally {
					ftpConManager.closeCon();
				}
			}
			
		return fileLink;
	}
	/**
	 * @describe 商场/店铺信息页面    
	 * 
	 *            
	 */
	public Object operMerchantPage(String id,BaseController c) {
		initialized(c);
		String userId=  SecurityUtil.decodeAES(id);
        List<Conditions> baseTypes = null;
        AsShop shop =asShopMapper.shopIdByuserId(Integer.parseInt(userId));
        Object floorList = asDictParamMapper.findByKeyCode(DictParam.PARAM_SHOP_FLOOR_OPT);
        Integer userType=shop.getType();
        if (floorList != null) {
            floorList = JSONArray.fromObject(floorList);
        }
        Object asShop = null;
        if(userType!=null && userType==1){
        	 asShop = asShopMapper.findShopInfoByShopId(shop.getShopId());
        	 baseTypes = ypBaseCategoryMapper.queryListAll(Conditions.newInstance().put("type", 5));
        }
        if(userType!=null &&userType==2){
       	     asShop = asShopMapper.findShopInfoByShopId(shop.getShopId());
       	 baseTypes = ypBaseCategoryMapper.queryListAll(Conditions.newInstance().put("type", 1));
       }
        List<AsShopPic> shopPic =asShopPicMapper.selectByShopId(shop.getShopId());
        Map<String, Object> shopMap = new HashMap<>();

        shopMap.put("baseTypes", baseTypes);
        shopMap.put("asShop", asShop);
        shopMap.put("shopPic", shopPic);
		return renderSuccess(0,new R(0, "", shopMap));

	}
	/**
	 * @describe 商场/店铺信息      保存/ 送审
	 * 
	 * 商户名称  行业类型（商场类型）  商户logo  商户二维码   商户背景图(主图) 联系人 联系电话  联系邮箱  商户地址（省 市 区） 商场  所在楼层  区域 店铺详细地址 （经纬度)
	 * @param tradeCategoryId
     * @param logoUrl
     * @param qrCodeUrl
     * @param mainPicUrl 
     * @param contact
     * @param tel
     * @param provinceId
     * @param cityId
     * @param areaId
     * @param tradeCategoryId
     * @param storeId
     * @param floor
     * @param address
     * @param lng
     * @param lat
	 * @throws Exception 
	 *            
	 */
	public Object saveMerchant(String ShopName,
			String tradeCategoryId,
			String logoUrl,
			String qrCodeUrl,
			String mainPicUrl,
			String contact,
			String tel, 
			Integer provinceId,
			Integer cityId, 
			Integer areaId,
			Integer businessDistrictId,
			Integer storeId,
			Integer floor,
			String address,
			String lng,
			String lat,
			String contactMail,
			String region,
			String token,String otype, BaseController c) throws Exception {
		initialized(c);
		AsUser user=null;  
		AsShop asShop =null;
		if(!StringUtils.isEmpty(otype)){
			if(otype.equals("2")){
			String userId=  SecurityUtil.decodeAES(token.toString());
			user = (AsUser) asUserMapper.selectByPrimaryKey(userId);
			}
		}
		if(!StringUtils.isEmpty(token)&&!otype.equals("2")){
			user = (AsUser) asUserMapper.selectByPrimaryKey(token);
			if (user == null)
			throw new Exception("系统繁忙");
		}
		SysUser SysUser = sysUserMapper.getSysUser(String.valueOf(user.getSysUserId()));
		asShop = asShopMapper.findByShopId(String.valueOf(SysUser.getAsShopId()));
		if (user == null)
			throw new Exception("系统繁忙");
		AsShopOwnerInfo shopOwnerInfo = (AsShopOwnerInfo)asShopOwnerInfoMapper.getShopOwnerByUserId(String.valueOf(user.getUserId()));
		//商场  & 店铺  & 街边店 storeId 等于0 为街边店
		if (user!=null && asShop!=null && user.getType() == 1 && storeId!=0) {
   
            @SuppressWarnings("unchecked")
			Map<String, String> ojb = (Map<String, String>) asStoreMapperr.getAddress(storeId);
            provinceId = CommonUtils.o2i(ojb.get("provinceId"));
            cityId = CommonUtils.o2i(ojb.get("cityId"));
            areaId = CommonUtils.o2i(ojb.get("areaId"));
            lng = String.valueOf(ojb.get("lng"));
            lat = String.valueOf(ojb.get("lat"));
            address = String.valueOf(ojb.get("address"));
            businessDistrictId = CommonUtils.o2i(ojb.get("businessDistrictId"));
        }
		//用户名称检查
		AsShop sp =new AsShop();
		sp.setName(ShopName);
		sp.setShopId(asShop.getShopId());
		operaFlag = asShopMapper.isMerchantAccountExists(sp) > 0;
        ValidatorBreakException.NOT_TRUE(!operaFlag, "商户名称已被注册！");
		asShop.setName(ShopName);
		asShop.setCreateTime(DateUtil.getTime());
		logoUrl=logoUrl==null||logoUrl.equals("")?null:logoUrl;
        asShop.setLogoUrl(uploadModule(logoUrl,UploadPathEnum.SHOP));
        qrCodeUrl=qrCodeUrl==null||qrCodeUrl.equals("")?null:qrCodeUrl;
		asShop.setQrCodeUrl(uploadModule(qrCodeUrl,UploadPathEnum.SHOPQRCODEURL));
		asShop.setContactMail(contactMail);
		asShop.setContact(contact);
		asShop.setTel(tel);
		asShop.setProvinceId(provinceId);
		asShop.setCityId(cityId);
		asShop.setAreaId(areaId);
		asShop.setBusinessDistrictId(businessDistrictId);
		asShop.setStoreId(storeId);
		asShop.setFloor(floor);
		asShop.setAddress(address);
		asShop.setLng(lng);
		asShop.setLat(lat);
		asShop.setCreateTime(DateUtil.getTime());
		asShop.setTradeCategoryId(Integer.parseInt(tradeCategoryId));
		region=region==null||region.equals("")?null:region;
		asShop.setRegion(region);
		if(asShop!=null){
        operaFlag = asShopMapper.updateByPrimaryKeySelective(asShop) == 1;
        ValidatorBreakException.NOT_TRUE(operaFlag);
		//删除之前的图片
        List<AsShopPic> picList = asShopPicMapper.selectByShopId(asShop.getShopId());
        JSONArray jsonArray = JSONArray.fromObject(mainPicUrl.equals("")?null:mainPicUrl);
         for (int i = 0; i < jsonArray.size(); i++) {
    		Map<String, Object> map = (Map<String, Object>) jsonArray.get(i);
    		Iterator<?> iterator = map.keySet().iterator();
    		while (iterator.hasNext()) {
			String key = (String) iterator.next();
			if (picList.size() > 0 && !(map.get(key).toString().equals(""))) {
			for (int K = 0; K < picList.size(); K++) {
			   if(key.equals(picList.get(K).getShopPicId().toString())){
			    picList.get(K).setShopId(asShop.getShopId());
				picList.get(K).setPicUrl(uploadModule(map.get(key).toString(),UploadPathEnum.SHOPMAINPICURL));
				asShopPicMapper.updateByPrimaryKeySelective(picList.get(K));
			   }else if((!StringUtils.isEmpty(map.get(key).toString()))){
			    AsShopPic pic = new AsShopPic();
				pic.setShopId(asShop.getShopId());
				pic.setPicUrl(uploadModule(map.get(key).toString(),
						UploadPathEnum.SHOPMAINPICURL));
				asShopPicMapper.insertSelective(pic);
			   }
			   }
			}
			if(picList.size() ==0 && !StringUtils.isEmpty(map.get(key).toString())){
				AsShopPic pic = new AsShopPic();
				pic.setShopId(asShop.getShopId());
				pic.setPicUrl(uploadModule(map.get(key).toString(),
						UploadPathEnum.SHOPMAINPICURL));
				asShopPicMapper.insertSelective(pic);
			}
		  } 	
    	  }
     if (!StringUtils.isEmpty(asShop.getName())) {
         String pinyin = PinyinUtil.converterToFirstSpell(asShop.getName());
         List<AsShopSpell> asShopSpell = asShopSpellMapper.selectByShopId(asShop.getShopId());
         if(asShopSpell==null||asShopSpell.size() ==0){
         if (pinyin.indexOf(",") > -1 ) {
             String[] str = pinyin.split(",");
             for (int j = 0; j < str.length; j++) {
        		 AsShopSpell spell = new AsShopSpell();
                 spell.setShopId(asShop.getShopId());
                 spell.setSpellValue(str[j]);
                 asShopSpellMapper.insertSelective(spell);
             }
         } if(!(pinyin.indexOf(",") > -1) && asShopSpell.size()==0) {
        	     AsShopSpell spell = new AsShopSpell();
                 spell.setShopId(asShop.getShopId());
                 spell.setSpellValue(pinyin);
                 asShopSpellMapper.insertSelective(spell);
         }
         }else{
        	 asShopSpellMapper.deleteByPrimaryKeyByshopId(asShop.getShopId());
        	 if (pinyin.indexOf(",") > -1 ) {
                 String[] str = pinyin.split(",");
                 for (int j = 0; j < str.length; j++) {
            		 AsShopSpell spell = new AsShopSpell();
                     spell.setShopId(asShop.getShopId());
                     spell.setSpellValue(str[j]);
                     asShopSpellMapper.insertSelective(spell);
                 }
             } if(!(pinyin.indexOf(",") > -1) && asShopSpell.size()>0) {
            	     AsShopSpell spell = new AsShopSpell();
                     spell.setShopId(asShop.getShopId());
                     spell.setSpellValue(pinyin);
                     asShopSpellMapper.insertSelective(spell);
             }
         }
         
         }
     }
     shopOwnerInfo.setName(asShop.getName());
     shopOwnerInfo.setTel(asShop.getTel());
     shopOwnerInfo.setEmail(contactMail);
     Integer auditStatus=auditStatus=shopOwnerInfo.getAuditStatus()==null?0:shopOwnerInfo.getAuditStatus();
     shopOwnerInfo.setAuditStatus(auditStatus.compareTo(2)==0?auditStatus:0);
     operaFlag = asShopOwnerInfoMapper.updateByPrimaryKeySelective(shopOwnerInfo) == 1;
        
	 return renderSuccess();
	}

	
	/**
	 * @describe 省份  
	 * 
	 *            
	 */
	public Object getProvince(BaseController c) {
		initialized(c);
		List<AsProvince> provinceList = asProvinceMapper.getProvinceAll();
		return renderSuccess(0,new R(0, "", provinceList));
	}
	/**
	 * @describe 城市
	 * 
	 *            
	 */
	public Object getCity(Integer provinceId, BaseController c) {
		initialized(c);
        List<AsCity> cityList = asCityMapper.getCitByProvinceId(provinceId);
		return renderSuccess(0,new R(0, "", cityList));
	}
	/**
	 * @describe 区域 
	 * 
	 *            
	 */
	public Object getArea(String cityId, BaseController c) {
		initialized(c);
        List<AsArea> areaList = asAreaMapper.getAreaByCityId(cityId);
		return renderSuccess(0,new R(0, "", areaList));
	}
	/**
	 * @describe 商圈
	 * 
	 *            
	 */
	public Object getBusinessDistrict(String areaId, BaseController c) {
		initialized(c);
        List<AsBusinessDistrict> businessDistrictList = asBusinessDistrictMapper.getBusinessDistrict(areaId);
		return renderSuccess(0,new R(0, "", businessDistrictList));
	}
	/**
	 * @describe 商场  
	 * 
	 *            
	 */
	public Object getStore(String businessDistrictId, BaseController c) {
		initialized(c);
        List<AsStore> storList = asStoreMapperr.getStore(businessDistrictId);
		return renderSuccess(0,new R(0, "", storList));
	}
	/**
	 * @describe 楼层  
	 * 
	 *            
	 */
	public Object getFloor(BaseController c) {
		initialized(c);
        Object floorList = asDictParamMapper.findByKeyCode(DictParam.PARAM_SHOP_FLOOR_OPT);
		return renderSuccess(0,new R(0, "", floorList));
	}
	/**
	 * @describe 行业类型  
	 * 
	 *            
	 */
	public Object getBaseType(Integer type, BaseController c) {
		initialized(c);
		List<AsYpBaseCategory> ypBaseCategory = ypBaseCategoryMapper.queryCategoryList(type);
		return renderSuccess(0,new R(0, "", ypBaseCategory));
	}

	public AsUser selectByPrimaryKey(int userId) {
			return (AsUser) asUserMapper.selectByPrimaryKey(userId);
		}
	/**
	 * @describe 所有商场  
	 * 
	 *            
	 */
	public Object getStoreAll(BaseController c) {
		initialized(c);
        List<AsStore> storList = asStoreMapperr.getStoreAll();
		return renderSuccess(0,new R(0, "", storList));
	}
	
	public Object deleteMainPic(Integer shopPicId,BaseController c) {
		initialized(c);
		operaFlag = asShopPicMapper.delteShopPicById(shopPicId)==0;
		ValidatorBreakException.NOT_TRUE(!operaFlag, "系统繁忙！");
	  return renderSuccess();

	}
	/**
	 * @describe 获取商户信息
	 */
	public Object getUserInfoByUserId(BaseController c,String userId) {
		initialized(c);
		Map<String,String> mapUser = new HashMap<String,String>();
		int auditStatus=1010;
		int merChanType = 0;
		String des = SecurityUtil.decodeAES(userId);
		Integer uid = Integer.parseInt(SecurityUtil.decodeAES(userId));
		AsUser asUser = asUserMapper.findUserBySysUserId(uid);
		if (asUser != null) {
		if (UserTypeEnum.SELLER.getId() == asUser.getType()) {
			Conditions asShopOwner = asShopOwnerInfoMapper.findByUserId(uid);
			ValidatorBreakException.NOT_NULL(asShopOwner);
			if(StringUtils.isEmpty(asShopOwner.get("auditStatus"))){
				auditStatus = -1;
			}
			if(asShopOwner.get("auditStatus")!=null){
				auditStatus = asShopOwner.getInt("auditStatus");
			}
			if(!Arrays.asList(Const.MERCHANT_LOGIN).contains(auditStatus)){
				ValidatorBreakException.NOT_TRUE(false, "帐号异常！");
			}
			if (UserTypeEnum.SELLER.getId() == asUser.getType()
					||UserTypeEnum.STAFF.getId() == asUser.getType()) {
				merChanType = asShopMapper.selectByPrimaryKey(String.valueOf(asShopOwner.getInt("shopId"))).getType();
			}
			switch (auditStatus) {
				case -1:
					auditStatus=1003;
					break;
				case 0:
					auditStatus=1004;
					break;
				case 1:
					auditStatus=1005;
					break;
				case 2:
					auditStatus=1006;
					break;
				case 3:
					auditStatus=1007;
					break;
				case 1010:
					auditStatus=1010;

			}
			mapUser.put("token",userId);
			mapUser.put("userType",String.valueOf(merChanType));
			mapUser.put("status",String.valueOf(auditStatus));
		}

		}else{
			ValidatorBreakException.NOT_TRUE(operaFlag, "账号不存在!");
		}
		return mapUser;
	}
}