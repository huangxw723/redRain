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
		<!--引入弹窗组件start-->
		<script type="text/javascript" src="plugins/attention/zDialog/zDrag.js"></script>
		<script type="text/javascript" src="plugins/attention/zDialog/zDialog.js"></script>
		<!--引入弹窗组件end-->
		<!--提示框-->
		<script type="text/javascript" src="static/js/jquery.tips.js"></script>
		<script type="text/javascript" src="static/js/common/commonutils.js"></script>
		
  		
<script type="text/javascript">
	$(top.hangge());
	$(function() {
		$(".chzn-select").chosen(); 
		$(".chzn-select-deselect").chosen({allow_single_deselect:true}); 
	});

function bankEdit(){
	var bankCode = $("#bankCode").val();
	var bankName = $("#bankName").val();
	if(null==bankCode||""==bankCode){
		bootbox.alert("请输入银行代码")
		return;
	}
	if(null==bankName||""==bankName){
		bootbox.alert("请输入银行名称")
		return;
	}if(!codeFlag){
		bootbox.alert("银行代码重复")
		return;
	}if(!nameFlag){
		bootbox.alert("银行名称重复")
		return;
	}
	
	/* var reg = new RegExp("^([1-9]\\d*|0)$");
	if(!reg.test(bankCode)){
		alert("银行代码只能为纯数字")
		$("#bankCode").val("");
		return;
	} */
	form.submit($('#bankEditForm'));

	//$("#bankEditForm").submit();
	//$("#bankEditForm").submit();
}



var codeFlag = true;
var nameFlag = true;
function checkBankByCodeOrName(flag) {
	var bankOldCode = $("#bankOldCode").val();
	var bankOldName = $("#bankOldName").val();
	var bankCode = $("#bankCode").val();
	var bankName = $("#bankName").val();

	if("1"==flag&&bankOldCode!=bankCode){
		 bankName = "";
		   $.ajax({
			    type : "post",
		        url : "bank/checkBankByCodeOrName",
		        dataType : "json",
				async: false,
				data : {
					bankCode:bankCode,
					bankName:bankName
				},
		        success : function (data) {
		        	if("1"==data.statusCode){
		        		bootbox.alert("银行代码重复")
		    			codeFlag = false;
		        		return;
		    	}else{
		    		codeFlag = true;
	        		return;
		    	}
		        }
		});
	}if("2"==flag&&bankOldName!=bankName){
		 bankCode = "";
		   $.ajax({
			    type : "post",
		        url : "bank/checkBankByCodeOrName",
		        dataType : "json",
				async: false,
				data : {
					bankCode:bankCode,
					bankName:bankName
				},
		        success : function (data) {
		        	if("1"==data.statusName){
		        	bootbox.alert("银行名称重复")
			    	nameFlag = false;
		    		return;
		    	}else{
		    		nameFlag = true;
		    		return;
		    	}
		    	
		        }
		});
	}
}








</script>
	</head>
<body>

	<form action="bank/bankEdit.do" name="bankEditForm" id="bankEditForm" method="post">
	<div id="zhongxin" >
	<table id="table_report" class="table table-striped table-hover" style="overflow:scroll;">
	     <div><h2>编辑银行</h2> 银行编号: ${bankCd.bankId}</div>
	<input name="bankId" id="bankId" type="hidden"  value="${bankCd.bankId}" />
	<input name="flag" id="flag" type="hidden"  value="" />
	
	     </br>
		<tr>
		
		 <tr height="45px">
			<td>银行代码:</td>
			<td><input  onchange="checkBankByCodeOrName('1')"   type="number" min="0" name="bankCode" id="bankCode" onkeyup="validateUtils.replaceNotNumEmpty(this);" value="${bankCd.bankCode}" style="width: 370px" />
			<input name="bankOldCode" id="bankOldCode" type="hidden"  value="${bankCd.bankCode}" />
		
		 </td>
		<tr height="45px">
			<td>银行名称：</td>
			<td><input  onchange="checkBankByCodeOrName('2')"   type="text" name="bankName" id="bankName" value="${bankCd.bankName}" style="width: 370px" /></td>
			<input name="bankOldName" id="bankOldName" type="hidden"  value="${bankCd.bankName}" />
			 
		</tr>
		<tr height="45px" >
			<td>状态：</td>
				<td>
		<select name="status" id="status" style="width: 380px">
					<c:if test="${bankCd.status == 0}">
					<option value="0" selected>启用</option>
					<option value="1">删除</option>
					</c:if>
					<c:if test="${bankCd.status == 1}">
					<option value="1" selected>删除</option>
					<option value="0">启用</option>
					</c:if>
		</select>
			</td>
		</tr>

		<tr height="45px">
		    <td>记录时间：</td>
				<td><input  style="background:;border:0;width:370px" disabled="disabled" type="text" name="createTime" id="createTime" value="${bankCd.createTime}" style="width: 370px" /></td>
		</tr>
	
        
		<tr>
          <td colspan="8" style="height: 30px;"> 
               <div style="text-align: center;">
                <a class="btn btn-mini btn-primary" id="a1" onclick="bankEdit();" style="width: 90px; height: 34px; line-height:34px">修改</a>
				<a class="btn btn-mini btn-danger" onclick="top.Dialog.close();" style="width: 90px; height: 34px; line-height:34px">取消</a>
				</div>
			</td>
			
		</tr>
	
	</table>
	</div>
	
	<div id="zhongxin2" class="center" style="display:none">
      <br />
      <br />
      <br />
      <br />
      <br />
      <img src="static/images/jiazai.gif" />
      <br />
      <h4 class="lighter block green">提交中...</h4>
    </div>
    
	</form>

	<!-- 引入 -->
	<script type="text/javascript">window.jQuery || document.write("<script src='static/js/jquery-1.9.1.min.js'>\x3C/script>");</script>
	<script src="static/js/bootstrap.min.js"></script>
	<script src="static/js/ace-elements.min.js"></script>
	<script src="static/js/ace.min.js"></script>
	<script type="text/javascript" src="static/js/chosen.jquery.min.js"></script><!-- 下拉框 -->
	<script type="text/javascript" src="static/js/bootbox.min.js"></script><!-- 确认窗口 -->
		
	<script type="text/javascript" src="static/js/base64.js"></script>
	<!-- 压缩图片 -->

	<link rel="stylesheet" type="text/css" href="plugins/fancyBox/jquery.fancybox.css">
	<script type="text/javascript" src="plugins/fancyBox/jquery.fancybox.pack.js"></script>
	<!-- 查看图片 -->
	<script type="text/javascript" src="static/js/chosen.jquery.min.js"></script><!-- 下拉框 -->
	<script type="text/javascript">
	$(function() {

		$(".fancybox").fancybox({
            helpers: {
                title: {
                    type: 'inside'
                }
            }
        });
		
	});
	
  </script>
</body>
</html>