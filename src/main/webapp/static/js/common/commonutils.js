
function setCookie(name,value){
	var Days = 30;
	var exp = new Date();
	exp.setTime(exp.getTime() + Days*24*60*60*1000);
	document.cookie = name + "="+ escape (value) + ";expires=" + exp.toGMTString();
}

function getCookie(name){
	var arr,reg=new RegExp("(^| )"+name+"=([^;]*)(;|$)");
	if(arr=document.cookie.match(reg))
		return unescape(arr[2]);
	else
		return null;
}

function delCookie(name){
	var exp = new Date();
	exp.setTime(exp.getTime() - 1);
	var cval=getCookie(name);
	if(cval!=null)
	document.cookie= name + "="+cval+";expires="+exp.toGMTString();
}
function isArray (obj) { 
	return Object.prototype.toString.call(obj) === '[object Array]'; 
	}

function checkFileSize(obj){
    var obj;
    try{
        obj=obj.files[0].size;
        if(obj>1024*1024*40){
            alert('文件大小超过40M ！');
            return false;
        }
    }catch(Exception){
        return true;
    }
    return true;
}
/**
 * 字段校验工具对象
 * 
 * @author yangyanchao
 * @date 2016年7月18日
 */
validateUtils = {
		
		// 判断值是否空
		isEmpty:function(value){
			
			var isEmpty = false
			if(!value || value.length == 0 || value.replace(/(^\s*)|(\s*$)/g,"").length == 0){
				isEmpty = true
			}
			
			return isEmpty
		},
		
		// 校验邮箱地址
		isMail:function(value){
			return(new RegExp(/^(?:[a-zA-Z0-9]+[_\-\+\.]?)*[a-zA-Z0-9]+@(?:([a-zA-Z0-9]+[_\-]?)*[a-zA-Z0-9]+\.)+([a-zA-Z]{2,})+$/).test(value));
		},
		
		// 校验手机号码
		isMobilePhone:function(value){
			return new RegExp(/^1[34578]\d{9}$/).test(value);
		},
		
		// 过滤空格
		replaceSpaceEmpty:function(element){
			
			var pattern = new RegExp(/^ +| +$/g);
			if(!pattern.test($(element)[0].value))
				return

			$(element).val($(element)[0].value.replace(/^ +| +$/g,''));
		},
		
		// 过滤非数字
		replaceNotNumEmpty:function(element){
			
			var pattern = new RegExp(/[^0-9]*/g);
			if(!pattern.test($(element)[0].value))
				return
				
			$(element).val($(element)[0].value.replace(/[^0-9]*/g,''));
		},
		
		// 判断是否不可编辑
		setDisabled:function(element,action){
			if(action == 4){
				$(element).attr("disabled","disabled");
			}
		},
		
		/**
		 * 校验日期部分是否合法 yyyy-MM-dd
		 * @param mystring 输入值
		 * @returns {Boolean}
		 */
		isDate:function(mystring) {  
			var reg = /^(\d{4})-(\d{2})-(\d{2})$/;  
			var str = mystring;  
			var arr = reg.exec(str);  
			if (!reg.test(str)&&RegExp.$2 <= 12&&RegExp.$3<=31){  
			
				return false;  
			}  
			
			return true;  
		}  
}

commonUtils = {
		// tab 切换
		changeTab:function(tabName,src){
			
			var frameId = tabName;
			if($('#'+frameId).length == 0){
    			
				var bheight = document.documentElement.clientHeight;as_prize
    			var frameSrc = src;
        		var height = (bheight - 110);
        		var frameEle = '<iframe myFrame name="'+frameId+'" id="'+frameId+'" frameborder="0" '+
        		'src="'+frameSrc+'" style="margin:0 auto;width:100%;height=100%" height="'+height.toString()+'px"></iframe>';
        		$(frameEle).appendTo($('#tabWrapper'));
        		
        		//$('a[data-target=#'+frameId+']').trigger("click");
        		console.log($('#tabWrapper').length);
    		}
			
			$.each($('iframe[myFrame]'),function(i,item){
    			if($(item).attr('id') != frameId){
    				$(item).hide();
    			}else{
    				$(item).attr('src',src);
    				$(item).show();
    			}
    		})
		}
}

/**
 * 区域联动工具
 * @author yangyanchao
 * @date 二2016年7月18日
 */
areaUtils = {
		
		// 地区初始化
		init:function(selectedCityId,selectedAreaId){
			
			console.log("地区数据初始化...")
			if($("#provinceId").val()) {
				
				var provinceId = $("#provinceId").val();
				
			    var setCityAttr = false;
			    var setAreaAttr = false;
			    
				// 获取城市列表
			    console.log("获取城市列表 ...")
			    var citySelect = $("#cityId");
				    citySelect.empty();
			    $.get("locality/apiAreaList",{provinceId:provinceId,tag:2},function(data){
			    	
			    	if(data.status != 0){
			    		alert(data.msg);
			    		return;
			    	}
				    
				    var cityList = data.msg;
				    $.each(cityList, function(i, list){
				    	
				    	var cid = list.cityId;
						var cname = list.cityName;
				    	var label = $("<option value='"+cid+"'>"+cname+"</option>");
						citySelect.append(label);
				    	var pid = list.provinceId;
				   
						if(selectedCityId == cid || (pid == provinceId && !selectedCityId)) {
							
							if(!setCityAttr){
								label.attr("selected", "selected");
								setCityAttr = true;
								
								// 获取区县列表
								// 清空area下拉框
								var areaSelect = $('#areaId');
								areaSelect.empty();
								console.log("获取区县列表 ...")
								$.get("locality/apiAreaList",{cityId:cid,tag:3},function(data){
							    	
									if(data.status != 0){
							    		alert(data.msg);
							    		return;
							    	}
									
							    	var areaList = data.msg;
								    $.each(areaList, function(i, list){
								    	
								    	var acid = list.cityId;
										var aid = list.areaId;
										var aname = list.areaName;
											var label = $("<option value='"+aid+"'>"+aname+"</option>");
											areaSelect.append(label);
											
											if(selectedAreaId == aid || (acid == cid && !selectedAreaId)) {
												
												if(!setAreaAttr){
													label.attr("selected", "selected");
													setAreaAttr = true;
												}
											}
										
								 	});
								    
								    areaSelect.trigger("liszt:updated");
							    },'json')
							}
							
							
						}
				 	});
				    citySelect.trigger("liszt:updated");
			    },'json')
			}
		},
		
		// 绑定拉取城市列表事件
		provinceSelectedEvent:function(){
			
			$("#provinceId").change(function(){
				
			    var provinceId = $("#provinceId").val();
			    var isExists = false;
			    
			    // 获取城市列表
			    var citySelect = $("#cityId");
			    citySelect.empty();
			    $.get("locality/apiAreaList",{provinceId:provinceId,tag:2},function(data){
			    	
			    	if(data.status != 0){
			    		alert(data.msg);
			    		return;
			    	}
			    	
				    var cityList = data.msg;
				    $.each(cityList, function(i, list){
				    	
				    	var cid = list.cityId;
						var cname = list.cityName;
						var pid = list.provinceId;
				    	var label = $("<option value='"+cid+"'>"+cname+"</option>");
						citySelect.append(label);
						
						if(!isExists){
							
							isExists = true;
							// 获取城市列表
							// 清空area下拉框
							var areaSelect = $('#areaId');
							areaSelect.empty();
							$.get("locality/apiAreaList",{cityId:cid,tag:3},function(data){
						    	
								if(data.status != 0){
						    		alert(data.msg);
						    		return;
						    	}
						    	var areaList = data.msg;
							    $.each(areaList, function(i, list){
							    	var acid = list.cityId;
							    	
									
										var aid = list.areaId;
										var aname = list.areaName;
										var label = $("<option value='"+aid+"'>"+aname+"</option>");
										areaSelect.append(label);
							 	});
							    
							    areaSelect.trigger("liszt:updated");
						    },'json')
						    
						}
				 	});
				    citySelect.trigger("liszt:updated");
			    },'json')
			    
			});
		},
		
		// 绑定拉取区县事件
		citySelectedEvent:function(){
			$("#cityId").change(function(){
			    var cid = $("#cityId").val();
			    
			    var areaSelect = $('#areaId');
				areaSelect.empty();
				$.get("locality/apiAreaList",{cityId:cid,tag:3},function(data){
			    	
					if(data.status != 0){
			    		alert(data.msg);
			    		return;
			    	}
			    	var areaList = data.msg;
				    $.each(areaList, function(i, list){
				    	var acid = list.cityId;
				    	var aid = list.areaId;
						var aname = list.areaName;
						var label = $("<option value='"+aid+"'>"+aname+"</option>");
						areaSelect.append(label);	
				 	});
				    
				    areaSelect.trigger("liszt:updated");
			    },'json')
			    
			});
		
		}
}

/**
 * 表单工具类
 * @author yangyanchao
 * @date 2016年9月20日14:17:50
 */
form = {
		
	submit:function(formElement){
		
		var operaArea = formElement.find('#operaArea');
		operaArea.hide();
		
		var iUrl = formElement.attr('action');
		var data = new Object();
		$.each(formElement.serializeArray(),function(index,item){
			
			var key = item.name;
			var value = item.value;
			data[key] = value;
		});
		
		$.ajax({
            type: "POST",
            url: iUrl,
            data: data,
            dataType: "json",
            success: function(data){
            	
            	alert(data.msg)
            	operaArea.show()
            	
            	if(data.status == 0){
            		
            		formElement[0].reset()
            		
            		var close = formElement.find($('#close')).val();
            		if(close == 0){
            			
            			var editor = CKEDITOR.instances.descp;
            			if(editor){
            				editor.setData("");
                			formElement.find($('#descp')).val('');
            			}
            			return;
            		}
            			
            		
            		var element = document.getElementById('zhongxin');
            		element.style.display = 'none';
        			top.Dialog.close();
            		
            	}
            	
            },
            error:function(data){
            	alert('系统繁忙')
            	operaArea.show()
            }
        });
	}
}