
var isReady=false;var onReadyCallbacks=[];
var isServiceReady=false;var onServiceReadyCallbacks=[];
var __uniConfig = {"pages":["pages/login/login","pages/rewarded-video/rewarded-video","pages/index/index","pages/xiaoxi/xiaoxi","pages/set/set","pages/alipay/alipay","pages/qudao/qudao","pages/qianbao/qianbao","pages/yaoqingma/yaoqingma","pages/bangzhu/bangzhu","pages/tixian/tixian","pages/tixianjilu/tixianjilu","pages/bangzhuitem/bangzhuitem","pages/bindphone/bindphone","pages/shejiao/shejiao","pages/fenhongjilu/fenhongjilu","pages/shourumingxi/shourumingxi","pages/yaoqingjilu/yaoqingjilu","pages/fenhongshouyi/fenhongshouyi","pages/play/play","pages/fenhongmao/fenhongmao","pages/shiming/shiming","pages/changename/changename","pages/xieyi/xieyi","pages/wanfa/wanfa","pages/mine/mine","pages/tuandui/tuandui","uni_modules/uni-upgrade-center-app/pages/upgrade-popup","uni_modules/uni-feedback/pages/opendb-feedback/opendb-feedback","uni_modules/uni-feedback/pages/opendb-feedback/list","pages/download-app/download-app"],"window":{"navigationBarTextStyle":"black","navigationBarTitleText":"网赚游戏","navigationBarBackgroundColor":"#F8F8F8","backgroundColor":"#FFFFFF"},"tabBar":{"color":"#888888","selectedColor":"#FD7155","borderStyle":"#ffffff","backgroundColor":"#ffffff","list":[{"pagePath":"pages/index/index","iconPath":"static/tabbar/index.png","selectedIconPath":"static/tabbar/indexcheck.png","text":"首页"},{"pagePath":"pages/tuandui/tuandui","iconPath":"static/tabbar/mmt.png","selectedIconPath":"static/tabbar/mmtcheck.png","text":"喵喵团"},{"pagePath":"pages/mine/mine","iconPath":"static/tabbar/wode.png","selectedIconPath":"static/tabbar/wodecheck.png","text":"我的"}]},"darkmode":false,"nvueCompiler":"uni-app","nvueStyleCompiler":"weex","renderer":"auto","splashscreen":{"alwaysShowBeforeRender":true,"autoclose":false},"appname":"drama","compilerVersion":"3.8.12","entryPagePath":"pages/login/login","networkTimeout":{"request":60000,"connectSocket":60000,"uploadFile":60000,"downloadFile":60000}};
var __uniRoutes = [{"path":"/pages/login/login","meta":{"isQuit":true},"window":{"navigationStyle":"custom","bounce":"none"}},{"path":"/pages/rewarded-video/rewarded-video","meta":{},"window":{"navigationBarTitleText":"cocos game html"}},{"path":"/pages/index/index","meta":{"isQuit":true,"isTabBar":true},"window":{"navigationStyle":"custom"}},{"path":"/pages/xiaoxi/xiaoxi","meta":{},"window":{"navigationBarTitleText":"消息中心","navigationBarBackgroundColor":"#FFFFFF","backgroundColor":"#F5F5F5","bounce":"none"}},{"path":"/pages/set/set","meta":{},"window":{"navigationBarTitleText":"设置","navigationBarBackgroundColor":"#FFFFFF","backgroundColor":"#F5F5F5","bounce":"none"}},{"path":"/pages/alipay/alipay","meta":{},"window":{"navigationBarTitleText":"我的支付宝","navigationBarBackgroundColor":"#FFFFFF","backgroundColor":"#F5F5F5","bounce":"none"}},{"path":"/pages/qudao/qudao","meta":{},"window":{"navigationBarTitleText":"渠道商邀请","navigationBarBackgroundColor":"#FFFFFF","backgroundColor":"#F5F5F5","bounce":"none"}},{"path":"/pages/qianbao/qianbao","meta":{},"window":{"navigationStyle":"custom","bounce":"none"}},{"path":"/pages/yaoqingma/yaoqingma","meta":{},"window":{"navigationBarTitleText":"我的邀请码","navigationBarBackgroundColor":"#FFFFFF","backgroundColor":"#F5F5F5","bounce":"none","titleNView":{"titleSize":"32rpx","buttons":[{"color":"#333333","fontSize":"28rpx","text":"我的邀请人","width":"auto"}]}}},{"path":"/pages/bangzhu/bangzhu","meta":{},"window":{"navigationBarTitleText":"帮助中心","navigationBarBackgroundColor":"#FFFFFF","backgroundColor":"#F5F5F5","bounce":"none"}},{"path":"/pages/tixian/tixian","meta":{},"window":{"navigationBarTitleText":"提现结果","navigationBarBackgroundColor":"#FFFFFF","backgroundColor":"#F5F5F5","bounce":"none"}},{"path":"/pages/tixianjilu/tixianjilu","meta":{},"window":{"navigationBarTitleText":"余额明细","navigationBarBackgroundColor":"#FFFFFF","backgroundColor":"#F5F5F5","enablePullDownRefresh":true,"onReachBottomDistance":"30px","bounce":"none"}},{"path":"/pages/bangzhuitem/bangzhuitem","meta":{},"window":{"navigationBarTitleText":"帮助中心","navigationBarBackgroundColor":"#FFFFFF","backgroundColor":"#F5F5F5","bounce":"none"}},{"path":"/pages/bindphone/bindphone","meta":{},"window":{"navigationBarTitleText":"绑定手机号","navigationBarBackgroundColor":"#FFFFFF","backgroundColor":"#F5F5F5","bounce":"none"}},{"path":"/pages/shejiao/shejiao","meta":{},"window":{"navigationBarTitleText":"社交信息","navigationBarBackgroundColor":"#FFFFFF","backgroundColor":"#F5F5F5","bounce":"none"}},{"path":"/pages/fenhongjilu/fenhongjilu","meta":{},"window":{"navigationBarTitleText":"今日分红记录","navigationBarBackgroundColor":"#FFFFFF","backgroundColor":"#F5F5F5","enablePullDownRefresh":true,"onReachBottomDistance":"30px","bounce":"none"}},{"path":"/pages/shourumingxi/shourumingxi","meta":{},"window":{"navigationBarTitleText":"喵喵团收入明细","enablePullDownRefresh":true,"onReachBottomDistance":"30px","navigationBarBackgroundColor":"#FFFFFF","backgroundColor":"#F5F5F5","bounce":"none"}},{"path":"/pages/yaoqingjilu/yaoqingjilu","meta":{},"window":{"navigationBarTitleText":"邀请记录","enablePullDownRefresh":true,"onReachBottomDistance":"30px","navigationBarBackgroundColor":"#FFFFFF","backgroundColor":"#F5F5F5"}},{"path":"/pages/fenhongshouyi/fenhongshouyi","meta":{},"window":{"navigationStyle":"custom","bounce":"none"}},{"path":"/pages/play/play","meta":{},"window":{"navigationBarTitleText":"怎么玩","navigationBarBackgroundColor":"#FFFFFF","backgroundColor":"#F5F5F5","bounce":"none"}},{"path":"/pages/fenhongmao/fenhongmao","meta":{},"window":{"navigationBarTitleText":"必得全球分红猫","navigationBarBackgroundColor":"#FFFFFF","backgroundColor":"#F5F5F5","bounce":"none"}},{"path":"/pages/shiming/shiming","meta":{},"window":{"navigationBarTitleText":"实名认证","navigationBarBackgroundColor":"#FFFFFF","backgroundColor":"#F5F5F5","bounce":"none"}},{"path":"/pages/changename/changename","meta":{},"window":{"navigationBarTitleText":"修改昵称","enablePullDownRefresh":false,"bounce":"none"}},{"path":"/pages/xieyi/xieyi","meta":{},"window":{"navigationBarTitleText":"","navigationBarBackgroundColor":"#FFFFFF","backgroundColor":"#F5F5F5","bounce":"none"}},{"path":"/pages/wanfa/wanfa","meta":{},"window":{"navigationBarTitleText":"喵喵团玩法","navigationBarBackgroundColor":"#FFFFFF","backgroundColor":"#F5F5F5","bounce":"none"}},{"path":"/pages/mine/mine","meta":{"isQuit":true,"isTabBar":true},"window":{"backgroundColor":"#F5F5F5","navigationStyle":"custom","bounce":"none"}},{"path":"/pages/tuandui/tuandui","meta":{"isQuit":true,"isTabBar":true},"window":{"navigationStyle":"custom","enablePullDownRefresh":true,"backgroundColorTop":"#F5F5F5","backgroundColorBottom":"#F5F5F5"}},{"path":"/uni_modules/uni-upgrade-center-app/pages/upgrade-popup","meta":{},"window":{"disableScroll":true,"backgroundColorTop":"transparent","background":"transparent","titleNView":false,"scrollIndicator":false,"animationType":"fade-in","animationDuration":200}},{"path":"/uni_modules/uni-feedback/pages/opendb-feedback/opendb-feedback","meta":{},"window":{"navigationBarTitleText":"意见反馈","enablePullDownRefresh":false}},{"path":"/uni_modules/uni-feedback/pages/opendb-feedback/list","meta":{},"window":{"navigationBarTitleText":"意见反馈列表","enablePullDownRefresh":false,"navigationBarBackgroundColor":"#FFFFFF"}},{"path":"/pages/download-app/download-app","meta":{},"window":{"navigationBarTitleText":"","enablePullDownRefresh":false}}];
__uniConfig.onReady=function(callback){if(__uniConfig.ready){callback()}else{onReadyCallbacks.push(callback)}};Object.defineProperty(__uniConfig,"ready",{get:function(){return isReady},set:function(val){isReady=val;if(!isReady){return}const callbacks=onReadyCallbacks.slice(0);onReadyCallbacks.length=0;callbacks.forEach(function(callback){callback()})}});
__uniConfig.onServiceReady=function(callback){if(__uniConfig.serviceReady){callback()}else{onServiceReadyCallbacks.push(callback)}};Object.defineProperty(__uniConfig,"serviceReady",{get:function(){return isServiceReady},set:function(val){isServiceReady=val;if(!isServiceReady){return}const callbacks=onServiceReadyCallbacks.slice(0);onServiceReadyCallbacks.length=0;callbacks.forEach(function(callback){callback()})}});
service.register("uni-app-config",{create(a,b,c){if(!__uniConfig.viewport){var d=b.weex.config.env.scale,e=b.weex.config.env.deviceWidth,f=Math.ceil(e/d);Object.assign(__uniConfig,{viewport:f,defaultFontSize:Math.round(f/20)})}return{instance:{__uniConfig:__uniConfig,__uniRoutes:__uniRoutes,global:void 0,window:void 0,document:void 0,frames:void 0,self:void 0,location:void 0,navigator:void 0,localStorage:void 0,history:void 0,Caches:void 0,screen:void 0,alert:void 0,confirm:void 0,prompt:void 0,fetch:void 0,XMLHttpRequest:void 0,WebSocket:void 0,webkit:void 0,print:void 0}}}});
