package cn.zhouyafeng.itchat4j.demo.demo2;

import cn.zhouyafeng.itchat4j.Wechat;
import cn.zhouyafeng.itchat4j.beans.BaseMsg;
import cn.zhouyafeng.itchat4j.beans.TuringRobotRequest;
import cn.zhouyafeng.itchat4j.beans.TuringRobotResponse;
import cn.zhouyafeng.itchat4j.core.Core;
import cn.zhouyafeng.itchat4j.face.IMsgHandlerFace;
import cn.zhouyafeng.itchat4j.utils.MyHttpClient;
import cn.zhouyafeng.itchat4j.utils.enums.MsgTypeEnum;
import cn.zhouyafeng.itchat4j.utils.tools.DownloadTools;
import com.alibaba.fastjson.JSON;
import org.apache.http.HttpEntity;
import org.apache.http.util.EntityUtils;

import java.io.File;
import java.util.*;
import java.util.logging.Logger;
import java.util.stream.Collectors;

/**
 * 图灵机器人示例
 * 
 * @author https://github.com/yaphone
 * @date 创建时间：2017年4月24日 上午12:13:26
 * @version 1.0
 *
 */
public class TulingRobot implements IMsgHandlerFace {
	Logger logger = Logger.getLogger("TulingRobot");
	MyHttpClient myHttpClient = Core.getInstance().getMyHttpClient();
	String url = "http://openapi.tuling123.com/openapi/api/v2";
	String apiKey = "16f9b29d1ffd42d09d189f64fdb8acd9"; // 这里是我申请的图灵机器人API接口，每天只能5000次调用，建议自己去申请一个，免费的:)

	@Override
	public String textMsgHandle(BaseMsg msg) {
		List<String> validUsers=new ArrayList<>();
		validUsers.add("@3b0d8c17f9eaacbb96b052d7e50d6856f8aa462f2ea306e29400fc9e20c0e65f");
		if(!validUsers.contains(msg.getFromUserName())){
			return null;
		}
		if(msg.isGroupMsg()){
			return null;
		}
		String result = "";
		String text = msg.getText();
		String fromUserName= msg.getFromUserName();
		Map<String, String> paramMap = new HashMap<String, String>();
		paramMap.put("key", apiKey);
		paramMap.put("info", text);
		paramMap.put("userid", "123456");
		String paramStr = JSON.toJSONString(paramMap);

		TuringRobotRequest request = new TuringRobotRequest();
		TuringRobotRequest.PerceptionBean perception = new TuringRobotRequest.PerceptionBean();
		TuringRobotRequest.PerceptionBean.InputTextBean inputTextBean = new TuringRobotRequest.PerceptionBean.InputTextBean();
		TuringRobotRequest.UserInfoBean userInfo = new TuringRobotRequest.UserInfoBean();
		userInfo.setApiKey(apiKey);
		userInfo.setUserId("123456");
		request.setUserInfo(userInfo);

		inputTextBean.setText(text);
		perception.setInputText(inputTextBean);
		request.setPerception(perception);
		String requestJson = JSON.toJSONString(request);
		try {
			HttpEntity entity = myHttpClient.doPost(url, requestJson);
			result = EntityUtils.toString(entity, "UTF-8");
//			JSONObject obj = JSON.parseObject(result);
//			if (obj.getString("code").equals("100000")) {
//				result = obj.getString("text");
//			} else {
//				result = "这些问题，我还不知道啦";
//			}

			TuringRobotResponse response = JSON.parseObject(result,TuringRobotResponse.class);
			List<TuringRobotResponse.ResultsBean> resultBeans=response.getResults();
			if(resultBeans!=null&&resultBeans.size()>0){
				TuringRobotResponse.ResultsBean resultsBean = resultBeans.stream().filter(r->"text".equals(r.getResultType())).collect(Collectors.toList()).get(0);
				result=resultsBean.getValues().getText();
			}

		} catch (Exception e) {
			logger.info(e.getMessage());
			result="这些问题，我还不知道啦";
		}
		return result;
	}

	@Override
	public String picMsgHandle(BaseMsg msg) {
		return null;
	}

	@Override
	public String voiceMsgHandle(BaseMsg msg) {
		if(msg.isGroupMsg()){
			return null;
		}
		String fileName = String.valueOf(new Date().getTime());
		String voicePath = "D://itchat4j/voice" + File.separator + fileName + ".mp3";
		DownloadTools.getDownloadFn(msg, MsgTypeEnum.VOICE.getType(), voicePath);
		return null;
	}

	@Override
	public String viedoMsgHandle(BaseMsg msg) {
		if(msg.isGroupMsg()){
			return null;
		}
		String fileName = String.valueOf(new Date().getTime());
		String viedoPath = "D://itchat4j/viedo" + File.separator + fileName + ".mp4";
		DownloadTools.getDownloadFn(msg, MsgTypeEnum.VIEDO.getType(), viedoPath);
		return null;
	}

	public static void main(String[] args) {
		IMsgHandlerFace msgHandler = new TulingRobot();
		Wechat wechat = new Wechat(msgHandler, "D://itchat4j/login");
		wechat.start();
	}

	@Override
	public String nameCardMsgHandle(BaseMsg msg) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void sysMsgHandle(BaseMsg msg) {
		// TODO Auto-generated method stub
	}

	@Override
	public String verifyAddFriendMsgHandle(BaseMsg msg) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String mediaMsgHandle(BaseMsg msg) {
		// TODO Auto-generated method stub
		return null;
	}

}
