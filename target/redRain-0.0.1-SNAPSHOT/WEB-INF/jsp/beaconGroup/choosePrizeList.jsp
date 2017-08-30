<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ page import="com.aoshi.util.PropertyUtils" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%
  String path = request.getContextPath();
  String basePath = request.getScheme()+"://"+request.getServerName()+":"+request.getServerPort()+path+"/";
  String httpUrl = PropertyUtils.getHTTP_URL();
%>
<!DOCTYPE html>
<html lang="en">
<head>
  <base href="<%=basePath%>">
  <!-- jsp文件头和头部 -->
  <meta http-equiv="Content-Type" content="text/html; charset=utf-8">

  <meta name="description" content="overview & stats" />
  <meta name="viewport" content="width=device-width, initial-scale=1.0" />
  <!-- basic styles -->
  <link href="static/css/bootstrap.min.css" rel="stylesheet" />
  <link href="static/css/bootstrap-responsive.min.css" rel="stylesheet" />
  <link rel="stylesheet" href="static/css/font-awesome.min.css" />
  <!-- page specific plugin styles -->
  <!-- 下拉框-->
  <link rel="stylesheet" href="static/css/chosen.css" />
  <!-- ace styles -->
  <link rel="stylesheet" href="static/css/ace.min.css" />
  <link rel="stylesheet" href="static/css/ace-responsive.min.css" />
  <link rel="stylesheet" href="static/css/ace-skins.min.css" />
  <script type="text/javascript" src="static/js/jquery-1.7.2.js"></script>
  <link rel="stylesheet" href="static/css/datepicker.css" /><!-- 日期框 -->
  <!--引入弹窗组件start-->
  <script type="text/javascript" src="plugins/attention/zDialog/zDrag.js"></script>
  <script type="text/javascript" src="plugins/attention/zDialog/zDialog.js"></script>
  <!--引入弹窗组件end-->
  <script type="text/javascript" src="static/js/jquery.tips.js"></script>
</head>
<body>

<div class="container-fluid" id="main-container">


  <div id="page-content" class="clearfix">

    <div class="row-fluid">


      <div class="row-fluid">

        <!-- 检索  -->
        <form action="link/beaconGroup/choosePrize.do" method="post" name="usersForm" id="usersForm" style="margin: 10px">
          <input type="hidden" id="prizeStr" name="prizeStr" value="${prizeStr}"/>
          <input type="hidden" id="idse" name="idse" value="${idse}"/>
          <input type="hidden" name="currentPage" id="currentPage" value="${page.currentPage}"/>
          <input type="hidden" name="showCount" id="showCount" value="${page.showCount}"/>
          <table>
            <tr>
              <td>
                  <span class="input-icon">
                    <span>礼品名称:</span>
                    <input autocomplete="off" id="nav-search-input" type="text" name="keyWord" value="${keyWord}" placeholder="这里礼品名称" style="margin-bottom: 0px"/>
                  </span>
              </td>
              <td>
                <a class="btn btn-small btn-success" style="line-height: 20px;margin-left: 5px" id="sear">查询</a>
              </td>
            </tr>
          </table>
        </form>
        <!-- 检索  -->


            <table id="table_report" class="table table-striped table-bordered table-hover">
              <thead>
                <tr>
                  <th class="center">
                    <label>
                      <input type="checkbox" id="zcheckbox" />
                      <span class="lbl"></span>
                    </label>
                  </th>
                  <th class="center">礼品编号</th>
                  <th class="center">礼品名称</th>
                 <!--  <th class="center">奖品等级</th>
                  <th class="center">概率（%）</th> -->
                  <th class="center">礼品类型</th>
                  <th class="center">礼品数量(余/总)</th>
                </tr>
              </thead>

          <tbody>

                <!-- 开始循环 -->
                <c:choose>
                  <c:when test="${not empty asPrizeConfigs}">
                    <c:forEach items="${asPrizeConfigs}" var="var" varStatus="vs">
                      <tr>
                        <td class='center' style="width: 30px;">
                          <label>
                           <c:if test="${fn:length(idse)>1 }">
                           <c:if test="${not empty idse}">
                         <input type='checkbox'  name='ids' id="sid1" <c:forEach items="${idse}" var="vad" varStatus="vs"> 
                         <c:if test="${var.prizeConfigId==vad}">checked="checked"</c:if>
                           </c:forEach>
                            value="${var.prizeConfigId}" class="isd" />
                            <span class="lbl"></span>
                           </c:if>
                           </c:if>
                      <c:if test="${fn:length(idseSize)==1}">
                            <input type='checkbox' name='ids' id="sid1"  <c:forEach items="${idse}" var="vad" varStatus="vs"> 
                         <c:if test="${var.prizeConfigId==vad}">checked="checked"</c:if>
                           </c:forEach>
                           value="${var.prizeConfigId}" class="isd" />
                            <span class="lbl"></span>
                            </c:if>
                          <c:if test="${fn:length(idseSize)==0}">
                            <input type='checkbox' name='ids' id="sid1" value="${var.prizeConfigId}" class="isd" />
                            <span class="lbl"></span>
                            </c:if>
                          </label>
                        </td>
                        <td class='center' style="max-width: 100px;overflow: hidden;white-space: nowrap;text-overflow: ellipsis;" title="${var.sn}">${var.sn}</td>
                        <td class='center' style="max-width: 100px;overflow: hidden;white-space: nowrap;text-overflow: ellipsis;" title="${var.name}">${var.name}</td>
                        <%-- <td class='center' style="max-width: 100px;overflow: hidden;white-space: nowrap;text-overflow: ellipsis;" title="${var.shakeName}">${var.shakeName}</td>
                        <td class='center' style="max-width: 100px;overflow: hidden;white-space: nowrap;text-overflow: ellipsis;" title="${var.probability}">${var.probability} </td> --%>
                        <td class='center' style="max-width: 100px;overflow: hidden;white-space: nowrap;text-overflow: ellipsis;" title="${var.categoryName}">${var.categoryName}</td>
                        <td class='center' style="max-width: 100px;overflow: hidden;white-space: nowrap;text-overflow: ellipsis;" title="${var.surplusCounts}/${var.counts}">${var.surplusCounts}/${var.counts}</td>
                      </tr>

                    </c:forEach>
                  </c:when>
                  <c:otherwise>
                    <tr class="main_info">
                      <td colspan="100" class="center">没有相关数据</td>
                    </tr>
                  </c:otherwise>
                </c:choose>


              </tbody>
            </table>

            <div class="page-header position-relative">
              <table style="width:100%;">
                <tr>
                  <td style="vertical-align:top;">
                      <a class="btn btn-small btn-success" onclick="makeAll('确定要选择选中的礼品吗?');">保存</a>
                      </a>
                  </td>
                  <td style="vertical-align:top;">
                    <div class="pagination" style="float: right;padding-top: 0px;margin-top: 0px;">${page.pageStr}</div>
                  </td>
                </tr>
              </table>
            </div>
          </form>
        </div>
        <!-- PAGE CONTENT ENDS HERE -->
      </div>
      <!--/row-->
    </div>
    <!--/#page-content-->
  </div>
  <!--/.fluid-container#main-container-->

<!-- 返回顶部  -->
<a href="#" id="btn-scroll-up" class="btn btn-small btn-inverse">
  <i class="icon-double-angle-up icon-only"></i>
</a>

<!-- 引入 -->
<script type="text/javascript">window.jQuery || document.write("<script src='static/js/jquery-1.9.1.min.js'>\x3C/script>");</script>
<script src="static/js/bootstrap.min.js"></script>
<script src="static/js/ace-elements.min.js"></script>
<script src="static/js/ace.min.js"></script>

<script type="text/javascript" src="static/js/chosen.jquery.min.js"></script><!-- 下拉框 -->
<script type="text/javascript" src="static/js/bootstrap-datepicker.min.js"></script><!-- 日期框 -->
<script type="text/javascript" src="static/js/bootbox.min.js"></script><!-- 确认窗口 -->
<!-- 引入 图片预览功能 -->
<link rel="stylesheet" type="text/css" href="plugins/fancyBox/jquery.fancybox.css">
<script type="text/javascript" src="plugins/fancyBox/jquery.fancybox.pack.js"></script>

<script type="text/javascript" src="static/js/jquery.tips.js"></script><!--提示框-->
<script type="text/javascript">

  $(top.hangge());
	//检索
	function search() {
		top.jzts();
		$("#usersForm").submit();
	}
    //删除元素
	Array.prototype.remove = function(dx) {
		if (isNaN(dx) || dx > this.length) {
			return false;
		}
		for (var i = 0, n = 0; i < this.length; i++) {
			if (this[i] != this[dx]) {
				this[n++] = this[i]
			}
		}
		this.length -= 1
	}
	var li = [];
	
	$('.isd').on(
			'click',
		function() {
			if ($(this).prop('checked')) {
				if (li.length != 0) {
					var vale = this.value;
					if (!li.indexOf(vale) > -1) {
						li.push(this.value);
					}
					$("#idse").val(li);
					var emp = $("#usersForm").attr("action");
					var v_action;
					if (emp.indexOf("?") > -1) {
						v_action = emp.substring(0, emp.indexOf("?") + 1)
								+ "li=" + li;
					} else {
						v_action = emp + "?li=" + li;
					}
					$("#usersForm").attr("action", v_action)
				}
				if (li.length == 0) {
					li.push(this.value);
					if (!$("#idse").val() == ""
							|| !($.trim($("#idse").val()) == "")) {
						var f = JSON.parse('${idse}');
						for (var id = 0; id < f.length; id++) {
							li.push(f[id]);
						}
					}
					$("#idse").val(li);
					var emp = $("#usersForm").attr("action");
					var v_action;
					if (emp.indexOf("?") > -1) {
						v_action = emp.substring(0, emp.indexOf("?") + 1)
								+ "li=" + li;
					} else {
						v_action = emp + "?li=" + li;

					}

					$("#usersForm").attr("action", v_action);
				}
			} else {
				if (li.length != 0) {
					var vale = this.value;
					if (li.indexOf(vale) > -1) {
						li.remove(li.indexOf(vale));
					}
					for (var aa = 0; aa < li.length; aa++) {
						if (li[aa] == this.value) {
							li.remove(aa); //删除下标为1的元素 
							$("#idse").val(li);
						}
					}
					$("#idse").val(li);
					var emp = $("#usersForm").attr("action");
					var v_action;
					if (emp.indexOf("?") > -1) {
						v_action = emp.substring(0, emp.indexOf("?") + 1)
								+ "li=" + li;
					} else {
						v_action = emp + "?li=" + li;
					}
					$("#usersForm").attr("action", v_action);
				}

			}
		});

	var ArrUtils = {
		mergeArray : function(arr1, arr2) {
			for (var i = 0; i < arr1.length; i++) {
			for (var j = 0; j < arr2.length; j++) {
				if (arr1[i] === arr2[j]) {
					arr1.splice(i, 1); //删除元素，从第i个位置，截取长度为1的元素
				}
			}
			}
			if (arr2[0] != null) {
			for (var i = 0; i < arr2.length; i++) {
				arr1.push(arr2[i]);
			}
			}
			return arr1;
		}
	}
     
	$(function() {
		var ovje = window.parent;
		var idse = '${idse}';
		var tid = JSON.parse(idse);
		var c = ArrUtils.mergeArray(li, tid);
		$("#idse").val(c);

	});
	var ar = [];
	//复选框
	$(function(){
        $('#zcheckbox').on('click',function(){
          if(this.checked){    //全选
            $("input[id^='sid1']").each(function(){
                   if($(this).prop('checked')){
               	   if (!(li.indexOf(this.value) > -1)) {
               	   $(this).prop('checked',true);
				     li.push(this.value);
			      }	
                  } 
                   if(!$(this).prop('checked')){
                   $(this).prop('checked',true);
               	   if (!(li.indexOf(this.value) > -1)) {
					     li.push(this.value);
				   }
                   }  
                })
                $("#idse").val(li);
            }else{    //全不选
              $("input[id^='sid1']").each(function(){
             	 if($(this).prop('checked')){
               	 if (li.indexOf(this.value) > -1) {
               		 li.remove(li.indexOf(this.value));
			     }
                 }
             	$("#idse").val(li);
              	$(this).prop('checked',false);
              })
           }
       });
	});
	//批量选择
	 function makeAll(msg){
	    bootbox.confirm(msg, function(result) {
	      if(result) {
	        var str = $("#idse").val();;
	        /* for(var i=0;i < document.getElementsByName('ids').length;i++)
	        {
	          if(document.getElementsByName('ids')[i].checked){
	            if(str=='') str += document.getElementsByName('ids')[i].value;
	            else str += ',' + document.getElementsByName('ids')[i].value;
	          }
	        } */
	        if(str==''){
	          bootbox.dialog("您没有选择任何内容!",
	                  [
	                    {
	                      "label" : "关闭",
	                      "class" : "btn-small btn-success",
	                      "callback": function() {
	                        //Example.show("great success");
	                      }
	                    }
	                  ]
	          );

	          $("#zcheckbox").tips({
	            side:3,
	            msg:'点这里全选',
	            bg:'#AE81FF',
	            time:8
	          });

	          return;
	        }else{
	          if(msg == '确定要选择选中的礼品吗?'){
	            top.jzts();
	            top.Dialog.getCheckboxPrizeId(str);
	            top.Dialog.close();
	          }
	        }
	      }
	    });
	  } 
 	
</script>

<script type="text/javascript">

  $(function() {
    $("#sear").click(function () {
      search()
    })
    //日期框
    $('.date-picker').datepicker();

    //下拉框
    $(".chzn-select").chosen();
    $(".chzn-select-deselect").chosen({allow_single_deselect:true});

    

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

