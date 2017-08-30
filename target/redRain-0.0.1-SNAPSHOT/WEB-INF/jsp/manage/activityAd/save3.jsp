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
		<link rel="stylesheet" href="static/css/bootstrap-datetimepicker.min.css" /><!-- 日期框 -->
		<%--<link rel="stylesheet" href="static/css/datepicker.css" />--%><!-- 日期框 -->
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
	
	//更改图片
    function changeImg(){
        $("#fileInput").trigger("click");
    }
	
	//保存
	function save(){
		var adPosType = $("#adPosType").val;
		if($("#adName").val()==""||$.trim($("#adName").val())=="" && ${action}==1){
			$("#adName").tips({
				side:3,
	            msg:'广告名称',
	            bg:'#AE81FF',
	            time:2
	        });
			$("#adName").focus();
			return false;
		}
		if('${action}'==1){
			var refId = $("#refId").val();
			if(validateUtils.isEmpty(refId)){
				$("#refId").tips({
					side:3,
					msg:'当前没有进行中的活动',
					bg:'#AE81FF',
					time:2
				});
				$("#refId").focus();
				return false;
			};
		}

		var atime = $("#adStartTime").val();
		var stime = $("#adEndTime").val();
		var  st= new Date(atime);
		var  et= new Date(stime);

		if($("#adStartTime").val()==""||$.trim($("#adStartTime").val())=="" && ${action}==1){
			$("#adStartTime").tips({
				side:3,
	            msg:'开始日期不能为空',
	            bg:'#AE81FF',
	            time:2
	        });
			$("#adStartTime").focus();
			return false;
		}
		if($("#adEndTime").val()==""||$.trim($("#adEndTime").val())=="" && ${action}==1){
			$("#adEndTime").tips({
				side:3,
	            msg:'结束日期不能为空',
	            bg:'#AE81FF',
	            time:2
	        });
			$("#adEndTime").focus();
			return false;
		}
		if(et.getTime() <= st.getTime()){
			$("#adEndTime").tips({
				side:3,
	            msg:'结束日期不能小于开始日期',
	            bg:'#AE81FF',
	            time:2
	        });
			return false;
		};
		
		var fileType = document.getElementById("fileInput").value.substr(document.getElementById("fileInput").value.lastIndexOf(".")).toLowerCase();//获得文件后缀名
		var url = $("#url").val();
		var img = $("#imgHref").attr("src");
		if (!img || url) {
			if (fileType != '.gif' && fileType != '.png' && fileType != '.jpg' && fileType != '.jpeg') {
				$("#fileInput").tips({
					side : 3,
					msg : '请上传图片格式的文件',
					bg : '#AE81FF',
					time : 2
				});
				$("#fileInput").val('');
				document.getElementById("fileInput").files[0] = '请选择图片';
				return;
			}
		}

		// 提交表单
		form.submit($('#usersForm'));
		
	}

function CheckPointActivityCount() {
	var adPosType =$("#adPosType").val();
	if(3==adPosType){
	$.ajax({
		    type : "post",
	        url : "activityAd/CheckPointActivityCount",
	        dataType : "json",
			async: false,
	        success : function(data) {
	        	if(data.status!=0){
	        		alert(data.msg);
					setTimeout("self.location=self.location",100);
	        		return ;
	        	}
	        }
	});	  }
} 	
</script>
	</head>
<body>

	<form action="activityAd/opera.do" name="usersForm" id="usersForm" method="post">
	<input type="hidden" name="adId" value="${pd.adId}"/>
	<input type="hidden" name="action" id="action" value="${action }">
	<input type="hidden" name="otype" id="otype" value="${otype}">
	<input type="hidden" name="adUrlType" id="adUrlType" value="0">
	<input type="hidden" name="adUrl" id="adUrl" value="${pd.adUrl}">
	<input type="hidden" name="adStartTime" id="adStartTime" value="${pd.adStartTime}">
	<input type="hidden" name="adEndTime" id="adEndTime" value="${pd.adEndTime}">
		<c:if test="${action==2}">
			<input type="hidden" name="adPosType"  value="${pd.adPosType}">
			<input type="hidden" name="refId"  value="${pd.refId}">
		</c:if>
	<input type="hidden" name="adSn" id="adSn" value="${pd.adSn}">
	<div id="zhongxin" >
	<table id="table_report" class="table table-striped table-bordered table-hover" style="overflow:scroll;">
	<%-- <c:if test="${action != null && action !=1 }">
	     <tr height="45px">
			<td>广告编号:</td>
			<td><input  type="text" name="adSn" id="adSn" value="${pd.adSn}" style="width: 370px" <c:if test="${action !=null && action != '' && action==4}">disabled="disabled"</c:if>></td>
		</tr>
    </c:if> --%>
		<tr height="45px">
			<td>广告名称:</td>
			<td><input  type="text" name="adName" id="adName" value="${pd.adName}" style="width: 370px" <c:if test="${action !=null && action != '' && action==4}">disabled="disabled"</c:if>/></td>
		</tr>
		
			<tr height="45px">
			<td>广告板块类型:</td>
			<td>
              <select style="width: 383px" name="adPosType" id="adPosType"  <c:if test="${action !=null && action != '' && action==4||action==2}">disabled="disabled"</c:if>>
				  <c:if test="${action==4||action == 2}">
					  <option value="4" <c:if test="${pd.adPosType==4}">selected="selected"</c:if>>E街E铺-活动-弹窗</option>
					  <option value="5" <c:if test="${pd.adPosType==5}">selected="selected"</c:if>>E街E铺-活动-悬浮</option>
				  </c:if>
				  <c:if test="${action==1}">
					  <option value="4" >E街E铺-活动-弹窗</option>
					  <option value="5" >E街E铺-活动-悬浮</option>
				  </c:if>
              </select>
          </td>
		</tr>


		<tr height="45px">
			<td>红包雨活动:</td>
			<td>
				<select  <c:if test="${action != 1}">disabled="disabled"</c:if>  id="refId" name="refId" style="width: 383px; ">
					<c:if test="${not empty asActivity}">
					<c:forEach items="${asActivity}" var="var" >
						<option value="${var.activityId}" <c:if test="${var.activityId eq pd.refId}">selected</c:if> startTime="${var.startTime}" endTime="${var.endTime}">${var.activityName}</option>
					</c:forEach>
					</c:if>
				</select>
			</td>
		</tr>
		<!--选择活动ID-->
		<%--<tr id="refIdTr" style="display:none;">
			<td>活动ID:</td>
			<td>
				<select name="refId" id="refId">
id
				</select>
			</td>
		</tr>
		<script>
			$(function(){
				selectActivity($('#adPosType')[0]);
			});

			function selectActivity(obj){

				var refId = ${pd.refId};
				$('#refId').empty();
				if($(obj).val() == 4 || $(obj).val() == 5){
					$.get("activityAd/getCurrentActivity",{},function(item){
						$.each(item,function(index,item){
							if(item.activityId == refId){
								$("<option value='"+item.activityId+"' selected='selected'>"+item.activityName+"</option>").appendTo("#refId");
							}else{
								$("<option value='"+item.activityId+"'>"+item.activityName+"</option>").appendTo("#refId");
							}

						});

						$('#refIdTr').show();
					},'json');
				}else{
					$('#refIdTr').hide();
					$('#refId').empty();
				}

			}
		</script>
--%>

		<%--<tr height="45px">
			<td>广告url类型:</td>
			<td>
              <select disabled="disabled" style="width: 383px" >
                  <option value="0" >app端</option>
              </select>
          </td>
		</tr>
		<tr height="45px">
			<td>广告url：</td>
			<td><input  type="text" name="adUrl" id="adUrl" value="${pd.adUrl}" style="width: 370px" <c:if test="${action !=null && action != '' && action==4}">disabled="disabled"</c:if>/></td>
		</tr>--%>
		
		<tr>
        <td>发布时间:</td>
          <td>
              <input class="span10 date-picker"    id="adStartTime1" type="text" value="${pd.adStartTime }" data-date-format="yyyy-mm-dd hh:ii:00" readonly="readonly" style="width:370px;" placeholder="开始日期"  disabled="disabled" />
            </td>
       </tr>
		 <td>结束时间:</td>
            <td>
              <input class="span10 date-picker"   id="adEndTime1" type="text" value="${pd.adEndTime }" data-date-format="yyyy-mm-dd hh:ii:00" readonly="readonly" style="width:370px;" placeholder="结束日期"  disabled="disabled" />
            </td>
		<tr>
			<td>发布图片:</td>
             <td>
            <c:if test="${pd == null || pd.adImg== '' || pd.adImg == null }">
              <a id="imgBox" href="javascript:void(0)" style="display:none;">
                <img src="" width="150" id="imgHref" />
              </a>
              <input type="file" id="fileInput" name="fileInput"  />
					<span style="color: red; width: 50px;" id="msgDiv" >注： 图片格式支持 : gif/png/jpg/jpeg    尺寸规格750px * 750px</span>
              		<span style="color: red; width: 50px;" id="msgDivScreen" >注： 图片格式支持 : gif/png/jpg/jpeg    尺寸规格120px * 120px</span>
            </c:if>
            <c:if test="${pd != null && pd.adImg != '' && pd.adImg != null }">
              <a id="imgBox" href="<%=httpUrl%>${pd.adImg}"  >
                <img src="<%=httpUrl%>${pd.adImg}" alt="${pd.adImg}" width="150" id="imgHref" />
              </a>
              <c:if test="${action !=null && action != '' && action!=4}">
              <input type="button" class="btn btn-mini btn-danger" value="更改" onclick="changeImg()" id="chImg" />
					  <span style="color: red; width: 50px;" id="msgDiv" >注：尺寸规格750px * 750px</span>
					  <span style="color: red; width: 50px;" id="msgDivScreen" >注：尺寸规格120px * 120px</span>
				  <input type="file" id="fileInput" name="fileInput" style="display:none;" />
              </c:if>
            </c:if>
            <input id="url" name="img" type="hidden" />
          </td> 
		</tr>
		
		<tr height="45px">
			<td>描述:</td>
			<td>
				 <textarea style="width:90%;height:50px" name="adDesc" id="adDesc" <c:if test="${action !=null && action != '' && action==4}">disabled="disabled"</c:if>>${pd.adDesc}</textarea>
			</td>
		</tr>
    <c:if test="${action != null && action != 4}">
			<tr id="operaArea">
				<td style="text-align: center;" colspan="2">
					<a class="btn btn-mini btn-primary" onclick="save();">保存</a>
					<a class="btn btn-mini btn-danger" onclick="top.Dialog.close();">取消</a>
				</td>
			</tr>
     </c:if>
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
	<%--<script type="text/javascript" src="static/js/bootstrap-datepicker.min.js"></script>--%><!-- 日期框 -->
	<script type="text/javascript" src="static/js/bootstrap-datetimepicker.js"></script><!-- 日期框 -->
	<script type="text/javascript" src="static/js/bootstrap-datetimepicker.zh-CN.js"></script><!-- 日期框 -->
	<script type="text/javascript" src="static/js/chosen.jquery.min.js"></script><!-- 下拉框 -->
	<script type="text/javascript" src="static/js/bootbox.min.js"></script><!-- 确认窗口 -->
		
	<script type="text/javascript" src="static/js/base64.js"></script>
	<!-- 压缩图片 -->

	<link rel="stylesheet" type="text/css" href="plugins/fancyBox/jquery.fancybox.css">
	<script type="text/javascript" src="plugins/fancyBox/jquery.fancybox.pack.js"></script>
	<!-- 查看图片 -->
	<script type="text/javascript" src="static/js/chosen.jquery.min.js"></script><!-- 下拉框 -->
	<script type="text/javascript">
	$(top.hangge());

	$(document).ready(function(){
		if($("#action").val()==1){
			$("#msgDiv").show();
			$("#msgDivScreen").hide();
		}
		if($("#action").val()==2){
			if('${pd.adPosType}' == 4){
				$("#msgDiv").show();
				$("#msgDivScreen").hide();
			}
			if('${pd.adPosType}' ==5){
				$("#msgDiv").hide();
				$("#msgDivScreen").show();
			}
		}
		$("#adPosType").change(function(){
			if($(this).val()&&$(this).val()==5){
				$("#msgDiv").hide();
				$("#msgDivScreen").show();
			}else{
				$("#msgDiv").show();
				$("#msgDivScreen").hide();
			}
		});

	});

	$(function() {

		var startTime =  $("#refId").find("option:selected").attr("startTime");
		$("#adStartTime").val(startTime);
		$("#adStartTime1").val(startTime);
		var endTime =  $("#refId").find("option:selected").attr("endTime");
		$("#adEndTime").val(endTime);
		$("#adEndTime1").val(endTime);

		//联动select事件
		$("#refId").change(function(){
			var startTime =  $("#refId").find("option:selected").attr("startTime");
			$("#adStartTime").val(startTime);
			$("#adStartTime1").val(startTime);
			var endTime =  $("#refId").find("option:selected").attr("endTime");
			$("#adEndTime").val(endTime);
			$("#adEndTime1").val(endTime);
		});

	});

	//上传
	$('#fileInput:visible').ace_file_input({
		no_file : '请选择图片 ...',
		btn_choose : '选择',
		btn_change : '更改',
		droppable : false,
		onchange : null,
		thumbnail : false,
		before_remove : function() {
			$("#url").val("");
			$("#imgBox").attr("href", "").hide();
			$("#imgHref").attr("src", "");
			return true;
		}
	});
	
	new img2base64("fileInput", "url", "imgHref", "imgBox");

	$("#imgBox").fancybox({
		helpers : {
			title : {
				type : 'inside'
			}
		}
	});

	$(function() {
		
		//单选框
		$(".chzn-select").chosen(); 
		$(".chzn-select-deselect").chosen({allow_single_deselect:true});

		$('#adStartTime').datetimepicker({
			minuteStep :1,
			language:  'zh-CN',
			autoclose: 1,
			format: "yyyy-mm-dd hh:ii:00",

		}).on('changeDate',function(e){
			var startTime = e.date;
			$('#adEndTime').datetimepicker('setStartDate',startTime);
		});

		$('#adEndTime').datetimepicker({
			minuteStep :1,
			language:  'zh-CN',
			autoclose : 1,
			format: "yyyy-mm-dd hh:ii:00",
			startDate: new Date(),
		}).on('changeDate',function(e){
			var endTime = e.date;
			$('#adStartTime').datetimepicker('setEndDate',endTime);
		});
	});
  </script>


</body>
</html>