<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="com.aoshi.util.PropertyUtils"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%
	String path = request.getContextPath();
	String basePath = request.getScheme()+"://"+request.getServerName()+":"+request.getServerPort()+path+"/";
	String httpUrl = PropertyUtils.getHTTP_URL();
%>
<!DOCTYPE html>
<html lang="en">
<head>
	<base href="<%=basePath%>">
	<meta charset="utf-8" />
	<title></title>
	<meta name="description" content="overview & stats" />
	<meta name="viewport" content="width=device-width, initial-scale=1.0" />
	<link href="static/css/bootstrap.min.css" rel="stylesheet" />
	<link href="static/css/bootstrap-responsive.min.css" rel="stylesheet" />
	<link rel="stylesheet" href="static/css/font-awesome.min.css" />
	<!-- 下拉框 -->
	<link rel="stylesheet" href="static/css/chosen.css" />
	<link rel="stylesheet" href="static/css/ace.min.css" />
	<link rel="stylesheet" href="static/css/ace-responsive.min.css" />
	<link rel="stylesheet" href="static/css/ace-skins.min.css" />
	<script type="text/javascript" src="static/js/jquery-1.7.2.js"></script>
	<link rel="stylesheet" href="static/css/datepicker.css" /><!-- 日期框 -->
	<script src="static/js/base64Compress.js"></script>
	<!--引入弹窗组件start-->
	<script type="text/javascript" src="plugins/attention/zDialog/zDrag.js"></script>
	<script type="text/javascript" src="plugins/attention/zDialog/zDialog.js"></script>
	<!--引入弹窗组件end-->
	<!--提示框-->
	<script type="text/javascript" src="static/js/jquery.tips.js"></script>
		<!-- 引入 图片预览功能 -->
	<link rel="stylesheet" type="text/css" href="plugins/fancyBox/jquery.fancybox.css">
	<script type="text/javascript" src="plugins/fancyBox/jquery.fancybox.pack.js"></script>
<script type="text/javascript">

	$(function() {

		//日期框
		$('.date-picker').datepicker();
		$(".fancybox").fancybox({
			helpers: {
				title: {
					type: 'inside'
				}
			}
		});

	});

	$("#imgBox").fancybox({
		helpers : {
			title : {
				type : 'inside'
			}
		}
	});
	
</script>
</head>
<body >

	<div id="zhongxin" >
		<table id="table_report" class="table table-striped table-bordered table-hover" style="overflow:scroll;">
        	<tr>
	           <th>景点名称:</th>
	           <td><input type="text"   value="${cate.name }" style="width:100%;" disabled="disabled" /></td>
        	</tr>
			
			<tr>
				<td>封面图:</td>
				<td>				
						<a id="imgBox" class="has" href="<%=httpUrl%>${cate.coverImg}" title="${cate.coverImg}">
							<img src="<%=httpUrl%>${cate.coverImg}" alt="${cate.coverImg}" width="500"  />
						</a>
				</td>
			</tr>
			
			<tr>
			<td colspan="2">宣传图</td>
							<c:forEach items="${imgs}" var="var" >
								<tr>
									<td class="center" width="300" colspan="2">
										<a class="fancybox" href="<%=httpUrl%>${var.imgUrl}">
											<img src="<%=httpUrl%>${var.imgUrl}" alt="${var.imgUrl}" width="700px" height="450px" />
										</a>
									</td>
								</tr>
							</c:forEach>
            </tr>
        
        <tr>
	      <th>地址:</th>
          <td><input type="text" value="${cate.province.provinceName }&nbsp${cate.city.cityName}&nbsp${cate.area.areaName}" style="width:100% " disabled="disabled"><td>
        </tr>
        
        <tr>
	          <th>地址:</th>
	          <td>
	            <div id="l-map"></div>
	            <input type="text" style="width:88%;" name="address" id="address" value="${cate.address }" placeholder="这里输入地址" title="地址"  disabled="disabled"/>
	            <input type="hidden" name="lng" id="lng" value="${cate.lng}" />
	            <input type="hidden" name="lat" id="lat" value="${cate.lat}" />
	            <div id="searchResultPanel" style="border:1px solid #C0C0C0;width:150px;height:auto; display:none;"></div>
	          </td>
         </tr>
		
		</tr>
          <th>简介:</th>
          <td><textarea  style="width:100%;height: 150px " disabled="disabled" >${cate.consultationDesc}</textarea></td>
        </tr>
       
        <tr>
          <th>星级:</th>
          <td><input type="text" value="${cate.starLevel}" style="width:100% " disabled="disabled"></td>
        </tr>
         
        <tr>
          <th>开放时间:</th>
          <td><input type="text" value="${cate.openTime}" style="width:100%" disabled="disabled"></td>
        </tr>

        <tr>
          <th>联系电话:</th>
          <td><input type="text" value="${cate.phone}" style="width:100%" disabled="disabled"></td>
        </tr>
        
         <tr>
          <th>地铁路线:</th>
          <td><textarea    style="width: 400px;height: 100px" disabled="disabled">${metro}</textarea>
        </tr>
         <tr>
          <th>公交路线:</th>
          <td><textarea   style="width: 400px;height: 100px" disabled="disabled">${bus}</textarea></td>
        </tr>
        
            
            
		</table>
		
		
		
<!-- 引入 -->
<script type="text/javascript">window.jQuery || document.write("<script src='static/js/jquery-1.9.1.min.js'>\x3C/script>");</script>
<script src="static/js/bootstrap.min.js"></script>
<script src="static/js/ace-elements.min.js"></script>
<script src="static/js/ace.min.js"></script>
<script type="text/javascript" src="static/js/bootstrap-datepicker.min.js"></script><!-- 日期框 -->
<script type="text/javascript" src="static/js/chosen.jquery.min.js"></script><!-- 下拉框 -->
<script type="text/javascript" src="static/js/bootbox.min.js"></script><!-- 确认窗口 -->

<script type="text/javascript" src="static/js/base64.js"></script>
<script type="text/javascript" src="static/js/base64Compress.js"></script>
<!-- 压缩图片 -->
<!-- 编辑框-->
<script type="text/javascript" charset="utf-8">window.UEDITOR_HOME_URL = "<%=path%>/plugins/ueditor/";</script>
<script type="text/javascript" charset="utf-8" src="plugins/ueditor/ueditor.config.js"></script>
<script type="text/javascript" charset="utf-8" src="plugins/ueditor/ueditor.all.js"></script>
<!-- 编辑框-->
<link rel="stylesheet" type="text/css" href="plugins/fancyBox/jquery.fancybox.css">
<script type="text/javascript" src="plugins/fancyBox/jquery.fancybox.pack.js"></script>
<!-- 查看图片 -->
<script type="text/javascript" src="static/js/chosen.jquery.min.js"></script><!-- 下拉框 -->
<script type="text/javascript" src="http://api.map.baidu.com/api?v=2.0&ak=jGRH9RCPqXgfsrr2jyMLzhk1jKactdvj"></script>
<style type="text/css">
    #l-map{height:500px;width:90%;}
    #r-result{width:100%;}
</style>

<script type="text/javascript">
//百度地图API功能
function G(id) {
	return document.getElementById(id);
}

var map = new BMap.Map("l-map");
map.centerAndZoom("广州",12);                   // 初始化地图,设置城市和地图级别。

var ac = new BMap.Autocomplete(    //建立一个自动完成的对象
	{"input" : "address"
	,"location" : map
});

ac.addEventListener("onhighlight", function(e) {  //鼠标放在下拉列表上的事件
	var str = "";
	var _value = e.fromitem.value;
	var value = "";
	if (e.fromitem.index > -1) {
		value = _value.province +  _value.city +  _value.district +  _value.street +  _value.business;
	}    
	str = "FromItem<br />index = " + e.fromitem.index + "<br />value = " + value;
	
	value = "";
	if (e.toitem.index > -1) {
		_value = e.toitem.value;
		value = _value.province +  _value.city +  _value.district +  _value.street +  _value.business;
	}    
	str += "<br />ToItem<br />index = " + e.toitem.index + "<br />value = " + value;
	G("searchResultPanel").innerHTML = str;
});

var myValue;
ac.addEventListener("onconfirm", function(e) {    //鼠标点击下拉列表后的事件
var _value = e.item.value;
	myValue = _value.province +  _value.city +  _value.district +  _value.street +  _value.business;
	G("searchResultPanel").innerHTML ="onconfirm<br />index = " + e.item.index + "<br />myValue = " + myValue;
	setPlace();
});

function setPlace(){
	map.clearOverlays();    //清除地图上所有覆盖物
	function myFun(){
		var pp = local.getResults().getPoi(0).point;    //获取第一个智能搜索的结果
		map.centerAndZoom(pp, 18);
		map.addOverlay(new BMap.Marker(pp));    //添加标注
	}
	var local = new BMap.LocalSearch(map, { //智能搜索
	  onSearchComplete: myFun
	});
	var myGeo = new BMap.Geocoder();
	// 将地址解析结果显示在地图上，并调整地图视野    
	myGeo.getPoint(myValue, function(point){
    	if (point) {
    		$("#lng").val(point.lng);
    		$("#lat").val(point.lat);
    		local.search(myValue);
      	}
  	});
}

if($("#address").val() && $("#lng").val() && $("#lat").val()) {
	myValue = $("#address").val();
	ac.setInputValue($("#address").val());
	setPlace();
}

</script>
</body>
</html>