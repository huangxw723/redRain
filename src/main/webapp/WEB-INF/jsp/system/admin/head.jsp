<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<div class="navbar navbar-inverse">
		  <div class="navbar-inner">
		   <div class="container-fluid">
			  <a class="brand"><small><i class="icon-leaf"></i> <%=request.getSession().getAttribute("SYSNAME") %></small> </a>
			  
			  <ul class="nav ace-nav pull-right">
					<li style="display: none;" class="grey">
						<a href="javascript:alert('预留功能,待开发');" class="dropdown-toggle" data-toggle="dropdown">
							<i class="icon-tasks"></i>
							<span class="badge">1</span>
						</a>
						<ul class="pull-right dropdown-navbar dropdown-menu dropdown-caret dropdown-closer">
							<li class="nav-header">
								<i class="icon-ok"></i> 4 任务完成
							</li>
							
							<li>
								<a href="javascript:alert('预留功能,待开发');">
									<div class="clearfix">
										<span class="pull-left">软件更新</span>
										<span class="pull-right">65%</span>
									</div>
									<div class="progress progress-mini"><div class="bar" style="width:65%"></div></div>
								</a>
							</li>
							
							<li>
								<a href="javascript:alert('预留功能,待开发');">
									<div class="clearfix">
										<span class="pull-left">软件更新</span>
										<span class="pull-right">35%</span>
									</div>
									<div class="progress progress-mini progress-danger"><div class="bar" style="width:35%"></div></div>
								</a>
							</li>
							
							<li>
								<a href="javascript:alert('预留功能,待开发');">
									<div class="clearfix">
										<span class="pull-left">运行测试</span>
										<span class="pull-right">15%</span>
									</div>
									<div class="progress progress-mini progress-warning"><div class="bar" style="width:15%"></div></div>
								</a>
							</li>
							
							<li>
								<a href="javascript:alert('预留功能,待开发');">
									<div class="clearfix">
										<span class="pull-left">Bug 修复</span>
										<span class="pull-right">90%</span>
									</div>
									<div class="progress progress-mini progress-success progress-striped active"><div class="bar" style="width:90%"></div></div>
								</a>
							</li>
							
							<li>
								<a href="javascript:alert('预留功能,待开发');">
									查看任务明细
									<i class="icon-arrow-right"></i>
								</a>
							</li>
						</ul>
					</li>
					<li class="purple">
						<a href="http://www.baidu.com" class="dropdown-toggle" data-toggle="dropdown" target="mainFrame">
							<i class="icon-bell-alt icon-animated-bell icon-only"></i>
							<span class="badge badge-important"><%=request.getSession().getAttribute("unreadMessageCounts") == null || request.getSession().getAttribute("unreadMessageCounts").equals(0) ? "" : request.getSession().getAttribute("unreadMessageCounts") %></span>
						</a>
						<ul class="pull-right dropdown-navbar navbar-pink dropdown-menu dropdown-caret dropdown-closer" id="messageUl">
							<li class="nav-header">
								<i class="icon-warning-sign"></i> 系统消息
							</li>
							<c:forEach items="${unreadMessageList}" var="var" varStatus="vs">
							<li>
								<a href="javascript:void(0);" onclick="messageDetail(${var.sysMessageId})">
									<div class="clearfix">
										<span class="pull-left"><i class="icon-comment btn btn-mini btn-pink"></i> ${var.title}</span>
										<!-- <span class="pull-right badge badge-info">+12</span> -->
									</div>
								</a>
							</li>
							</c:forEach>
																
							<li>
								<a href="javascript:void(0)" target="mainFrame" target="mainFrame" 
								 onclick="siMenu('z1','lm1','系统消息','yp/admin/message/getAllMessageListAll')">
									查看所有消息
									<i class="icon-arrow-right"></i>
								</a>
							</li>
						</ul>
					</li>
					<li style="display: none;" class="green">
						<a href="javascript:alert('预留功能,待开发');" class="dropdown-toggle" data-toggle="dropdown">
							<i class="icon-envelope-alt icon-animated-vertical icon-only"></i>
							<span class="badge badge-success">1</span>
						</a>
						<ul class="pull-right dropdown-navbar dropdown-menu dropdown-caret dropdown-closer">
							<li class="nav-header">
								<i class="icon-envelope"></i> 5 条信件
							</li>
							
							<li>
								<a href="javascript:alert('预留功能,待开发');">
									<img alt="Alex's Avatar" class="msg-photo" src="static/avatars/avatar.png" />
									<span class="msg-body">
										<span class="msg-title">
											<span class="blue">张三:</span>
											你好，我们在哪里吃饭? ...
										</span>
										<span class="msg-time">
											<i class="icon-time"></i> <span>1个月以前</span>
										</span>
									</span>
								</a>
							</li>
							
							<li>
								<a href="javascript:alert('预留功能,待开发');">
									<img alt="Susan's Avatar" class="msg-photo" src="static/avatars/avatar3.png" />
									<span class="msg-body">
										<span class="msg-title">
											<span class="blue">李四:</span>
											你在哪上班? ...
										</span>
										<span class="msg-time">
											<i class="icon-time"></i> <span>20分钟前</span>
										</span>
									</span>
								</a>
							</li>
							
							<li>
								<a href="javascript:alert('预留功能,待开发');">
									<img alt="Bob's Avatar" class="msg-photo" src="static/avatars/avatar4.png" />
									<span class="msg-body">
										<span class="msg-title">
											<span class="blue">王五:</span>
											你好，我对你很感兴趣 ...
										</span>
										<span class="msg-time">
											<i class="icon-time"></i> <span>下午 3:15 </span>
										</span>
									</span>
								</a>
							</li>
							
							<li>
								<a href="javascript:alert('预留功能,待开发');">
									查看所有信件
									<i class="icon-arrow-right"></i>
								</a>
							</li>									
	
						</ul>
					</li>
					<li class="light-blue user-profile">
						<a class="user-menu dropdown-toggle" href="javascript:alert('预留功能,待开发');" data-toggle="dropdown">
							<img alt="FH" src="static/avatars/user.jpg" class="nav-user-photo" />
							<span id="user_info">
								
							</span>
							<i class="icon-caret-down"></i>
						</a>
						<ul id="user_menu" class="pull-right dropdown-menu dropdown-yellow dropdown-caret dropdown-closer">
							<!-- <li><a onclick="editUserH();" style="cursor:pointer;"><i class="icon-user"></i> 修改资料</a></li> -->
							<!-- <li id="systemset"><a onclick="editSys();" style="cursor:pointer;"><i class="icon-cog"></i> 系统设置</a></li>
							<li id="productCode"><a onclick="productCode();" style="cursor:pointer;"><i class="icon-cogs"></i> 代码生成</a></li>
							<li id="productCode_auto"><a onclick="productCode_auto();" style="cursor:pointer;"><i class="icon-cogs"></i> 代码生成_反向表</a></li>
							<li><a onclick="editUserH();" style="cursor:pointer;"><i class="icon-user"></i> 修改密码</a></li> -->
							<li><a href="javascript:void(0);" onclick="EditPassword()"><i class="icon-cog"></i> 修改密码</a></li>
                            <li class="divider"></li>
							<li><a href="logout"><i class="icon-off"></i> 退出</a></li>
							
						</ul>
					</li>
			  </ul><!--/.ace-nav-->
		   </div><!--/.container-fluid-->
		  </div><!--/.navbar-inner-->
		</div><!--/.navbar-->
	
	
		<!--引入属于此页面的js -->
		<script type="text/javascript" src="static/js/myjs/head.js"></script>
