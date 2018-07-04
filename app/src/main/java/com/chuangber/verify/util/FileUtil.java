package com.chuangber.verify.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.AssetManager;
import android.os.Environment;
import android.util.Log;

import com.chuangber.verify.application.ConStant;
import com.chuangber.verify.bean.IDCardInfoHd;
import com.huashi.otg.sdk.IDCardInfo;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

public class FileUtil {
	public static final String TAG = "FileUtil";
	public static final File parentPath = Environment.getExternalStorageDirectory();
	public static String storagePath = "";
	public static final String DST_FOLDER_NAME = "cloudwalk";

	static final String[] nation = new String[]{"汉", "蒙古", "回", "藏", "维吾尔", "苗", "彝", "壮", "布依", "朝鲜", "满", "侗", "瑶", "白", "土家", "哈尼", "哈萨克", "傣", "黎", "傈僳", "佤", "畲", "高山", "拉祜", "水", "东乡", "纳西", "景颇", "克尔克孜", "土", "达斡尔", "仫佬", "羌", "布朗", "撒拉", "毛南", "仡佬", "锡伯", "阿昌", "普米", "塔吉克", "怒", "乌兹别克", "俄罗斯", "鄂温克", "德昂", "保安", "裕固", "京", "塔塔尔", "独龙", "鄂伦春", "赫哲", "门巴", "珞巴", "基诺", "穿青衣", "其他", "外国血统中国籍人士"};
	public static void initHsXml(IDCardInfo cardInfo, String success, String fileName,String xmlPath){
		String head = "<?xml version='1.0' encoding='utf-8'?>";
		String node = "<GUEST>";
	StringBuffer sb = new StringBuffer();
	int length = nation.length;
	int nationCode = 1;
	String nationString = null;
	for (int i = 0; i<length;i++){
		if (cardInfo.getPeople().equals(nation[i])){
			nationCode = i+1;
			if(nationCode < 10){
				nationString = "0"+nationCode;
			}else {
				nationString = String.valueOf(nationCode);
			}
			break;
		}
	}
	String sex;

	sex = cardInfo.getSex().equals("男")?"1":"2";
	sb.append(head)
			.append(node)
			.append("<NAME>"+cardInfo.getPeopleName()+"</NAME>")
			.append("<SEX>"+sex+"</SEX>")
			.append("<PAPERID>"+cardInfo.getIDCard()+"</PAPERID>")
			.append("<NATION>"+nationString+"</NATION>")
			.append("<BIRTHDAY>"+cardInfo.getIDCard().substring(6,14)+"</BIRTHDAY>")
			.append("<ISSUCCESS>"+success+"</ISSUCCESS>")
			.append("<DADDR>"+cardInfo.getAddr()+"</DADDR>")
			.append("<PHOTO>"+xmlPath+"\\PHOTO.jpg</PHOTO>")
			.append("<ZPPHOTO>"+xmlPath+"\\ZPHOTO.jpg</ZPPHOTO>")
			.append("</GUEST>");
	writeTextToFile(sb.toString(),fileName);
	}

	public static void initHdXml(IDCardInfoHd cardInfo, String success, String fileName,String xmlPath){
		String head = "<?xml version='1.0' encoding='utf-8'?>";
		String node = "<GUEST>";
		StringBuffer sb = new StringBuffer();
		int length = nation.length;
		int nationCode = 1;
		String nationString = null;
		for (int i = 0; i<length;i++){
			if (cardInfo.getPeople().equals(nation[i])){
				nationCode = i+1;
				if(nationCode < 10){
					nationString = "0"+nationCode;
				}else {
					nationString = String.valueOf(nationCode);
				}
				break;
			}
		}
		String sex;

		sex = cardInfo.getSex().equals("男")?"1":"2";
		sb.append(head)
				.append(node)
				.append("<NAME>"+cardInfo.getPeopleName().trim()+"</NAME>")
				.append("<SEX>"+sex+"</SEX>")
				.append("<PAPERID>"+cardInfo.getIDCard()+"</PAPERID>")
				.append("<NATION>"+nationString+"</NATION>")
				.append("<BIRTHDAY>"+cardInfo.getIDCard().substring(6,14)+"</BIRTHDAY>")
				.append("<ISSUCCESS>"+success+"</ISSUCCESS>")
				.append("<DADDR>"+cardInfo.getAddr()+"</DADDR>")
				.append("<PHOTO>"+xmlPath+"\\PHOTO.jpg</PHOTO>")
				.append("<ZPPHOTO>"+xmlPath+"\\ZPHOTO.jpg</ZPPHOTO>")
				.append("</GUEST>");
		writeTextToFile(sb.toString(),fileName);

	}

	//初始化设备信息
	public static void initDeviceXml(Context context,String fileName){
		String head = "<?xml version='1.0' encoding='utf-8'?>";
		String node = "<DEVICE>";
		StringBuffer sb = new StringBuffer();
		sb.append(head)
				.append(node)
				.append("<machineId>"+SharePreUtil.getStringData(context,ConStant.STORAGE.MACHINE_ID,"default"))
				.append("</machineId>")
				.append("<hotelId>"+SharePreUtil.getStringData(context,ConStant.STORAGE.HOTEL_ID,"default"))
				.append("</hotelId>")
				.append("<count>")
				.append(1000)
				.append("</count>")
				.append("</DEVICE>");
		writeStringToFile(sb.toString(),fileName);
	}

	//修改设备信息
	public static void saveDeviceXml(SharedPreferences sharedPreferences,String fileName){
		int count = sharedPreferences.getInt(ConStant.STORAGE.ACCOUNT_REMAIN,1000);
		count = count-1;
		SharedPreferences.Editor editor = sharedPreferences.edit();
		editor.putInt(ConStant.STORAGE.ACCOUNT_REMAIN,count);
		editor.apply();
		String head = "<?xml version='1.0' encoding='utf-8'?>";
		String node = "<DEVICE>";
		StringBuffer sb = new StringBuffer();
		sb.append(head)
				.append(node)
				.append("<machineId>"+sharedPreferences.getString(ConStant.STORAGE.MACHINE_ID,"default"))
				.append("</machineId>")
				.append("<hotelId>"+sharedPreferences.getString(ConStant.STORAGE.HOTEL_ID,"default"))
				.append("</hotelId>")
				.append("<count>")
				.append(count)
				.append("</count>")
				.append("</DEVICE>");
		writeStringToFile(sb.toString(),fileName);
	}

//	facePhotoStr=new 
//			StringBuilder(parentPath.getAbsolutePath())
//			 .append(File.separator).append(DST_FOLDER_NAME).append(File.separator).toString();
	public static byte[] file2byte(File file) throws IOException {
		byte[] bytes = null;
		if (file != null) {
			InputStream is = new FileInputStream(file);
			int length = (int) file.length();
			if (length > Integer.MAX_VALUE) {// 褰撴枃浠剁殑闀垮害瓒呰繃浜唅nt鐨勬渶澶у��
				System.out.println("this file is max ");
				is.close();
				return null;
			}
			bytes = new byte[length];
			int offset = 0;
			int numRead = 0;
			while (offset < bytes.length && (numRead = is.read(bytes, offset, bytes.length - offset)) >= 0) {
				offset += numRead;
			}
			is.close();
			// 濡傛灉寰楀埌鐨勫瓧鑺傞暱搴﹀拰file瀹為檯鐨勯暱搴︿笉涓�鑷村氨鍙兘鍑洪敊浜�
			if (offset < bytes.length) {
				System.out.println("file length is error");
				return null;
			}
		}
		return bytes;
	}
	public static void mkParentDir(String path) {
		File file=new File(path);
		if(!file.exists())
			file.mkdir();
	}
	/**
	 * 閫掑綊鍒涘缓鏂囦欢澶�
	 * @param dirPath
	 */
	public static void mkDir(String dirPath) {
		String[] dirArray=dirPath.split("/");
		String pathTemp = "";
		for(int i=1;i<dirArray.length;i++){
			pathTemp=pathTemp+"/"+dirArray[i];
			File newF=new File(dirArray[0]+pathTemp);
			if(!newF.exists()){
				newF.mkdir();
			}
		}
	}
	
	public static void createModelFile(String modelDirPath, String modelFileName, AssetManager assetManager) {
		String content=null;
		String parentDir="model";
		List<String> fileNameList=null;
		String[] files = null;
		
		String fileAbsPath="";
		if(modelDirPath.endsWith("/")){
			fileAbsPath=modelDirPath+modelFileName;
		}else{
			fileAbsPath=modelDirPath+"/"+modelFileName;
		}
		
		File modelFile=new File(fileAbsPath);
		if(modelFile.exists()){
			return;
		}
		mkDir(modelDirPath);
		
		try {
			files = assetManager.list(parentDir);
			fileNameList= Arrays.asList(files);
		} catch (IOException e) {
			Log.e(TAG, e.getMessage());
		}
		
		for(String fileName: fileNameList){
			Log.e(TAG,fileName);
			content=readRawFileToString(parentDir+ File.separator+fileName , assetManager);
			writeStringToFile(content,fileAbsPath);
		}
	}
	
		public static void createModelFileAll(String modelDirPath, AssetManager assetManager) {
			String parentDir="model";
			List<String> fileNameList=null;
			String[] files = null;
			
			String fileAbsPath="";
			if(modelDirPath.endsWith("/")){
				fileAbsPath=modelDirPath;
			}else{
				fileAbsPath=modelDirPath+"/";
			}

			mkDir(modelDirPath);
			try {
				files = assetManager.list(parentDir);
				fileNameList= Arrays.asList(files);
			} catch (IOException e) {
				Log.e(TAG, e.getMessage());
			}
			
			for(String fileName: fileNameList){
				Log.e(TAG,fileName);
				File modelFile=new File(fileAbsPath+fileName);
				if(!modelFile.exists()){	
					copyRawFileToSdcard(parentDir+ File.separator+fileName , assetManager, fileAbsPath+fileName);
				}
			}
		}
	
	public static String readRawFileToString(String rawFileName , AssetManager assetManager) {
		InputStream inputStream = null;
		try {
			inputStream = assetManager.open(rawFileName);
		} catch (IOException e) {
			Log.e("tag", e.getMessage());
		}
		
		if(inputStream!=null)
			return inputStreamToString(inputStream);
		return null;
	}
	
	public static byte[] readRawFileToByteArray(String rawFileName , AssetManager assetManager) {
		InputStream inputStream = null;
		try {
			inputStream = assetManager.open(rawFileName);
		} catch (IOException e) {
			Log.e("tag", e.getMessage());
		}
		
		if(inputStream!=null)
			return inputStreamToByteArray(inputStream);
		return null;
	}
	
	public static void copyRawFileToSdcard(String rawFileName , AssetManager assetManager, String outPutFileAbs) {
		InputStream inputStream = null;
		try {
			inputStream = assetManager.open(rawFileName);
		} catch (IOException e) {
			Log.e("tag", e.getMessage());
		}
		
		if(inputStream!=null)
			inputStreamToFile(inputStream, outPutFileAbs);
	}
	
	public static String inputStreamToString(InputStream inputStream) {
		ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
		byte buf[] = new byte[1024];
		int len=0;
		try {
			while ((len = inputStream.read(buf)) != -1) {
				outputStream.write(buf, 0, len);
			}
			outputStream.close();
			inputStream.close();
		} catch (IOException e) {
		}
		return outputStream.toString();
	}
	
	public static byte[] inputStreamToByteArray(InputStream inputStream) {
		ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
		byte buf[] = new byte[1024];
		int len=0;
		try {
			while ((len = inputStream.read(buf)) != -1) {
				outputStream.write(buf, 0, len);
			}
			outputStream.close();
			inputStream.close();
		} catch (IOException e) {
		}
		return outputStream.toByteArray();
	}
	
	public static void inputStreamToFile(InputStream inputStream, String absPath) {
		ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
		byte buf[] = new byte[1024];
		int len=0;
		try {
			while ((len = inputStream.read(buf)) != -1) {
				outputStream.write(buf, 0, len);
			}
			outputStream.close();
			inputStream.close();
			
			byte[] imgBytes = outputStream.toByteArray();
			FileOutputStream fos=new FileOutputStream(absPath, false);
			fos.write(imgBytes);
			fos.close();
		} catch (IOException e) {
			e.printStackTrace();  
		}
	}
	
	public static void writeStringToFile(String content, String file_name) {
		try {  
			File file=new File(file_name);
			
			FileOutputStream fileW = new FileOutputStream(file.getCanonicalPath(),true);
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			String startTime = sdf.format(new Date(System.currentTimeMillis()));
			String log = startTime+"\r\n"+content+"\r\n\r\n";
            fileW.write(log.getBytes());
            fileW.close();  

		} catch (Exception e) {
			// TODO: handle exception  
		}  
	}

	public static void writeTextToFile(String content, String file_name) {
		try {
			File file = new File(file_name);
			FileOutputStream fileW = new FileOutputStream(file);
			fileW.write(content.getBytes());
			fileW.close();

		} catch (Exception e) {
			// TODO: handle exception
		}
	}

	public static void writeStringToFileNoAppend(String content, String file_name) {
		try {
			File file=new File(file_name);

			FileOutputStream fileW = new FileOutputStream(file.getCanonicalPath(),false);
			fileW.write(content.getBytes());
			fileW.close();

		} catch (Exception e) {
			// TODO: handle exception
		}
	}
	public static void writeByteArrayToFile(byte[] content, String file_name) {
		try {
			
			File file=new File(file_name);
			
			FileOutputStream fileW = new FileOutputStream(file.getCanonicalPath());
            fileW.write(content);  
            fileW.close();  

		} catch (Exception e) {
			// TODO: handle exception  
		}  
	}
	
	public static void writeByteArrayToSD(byte[] content, String file_name) {
		try {  
					
			File parentPath = Environment.getExternalStorageDirectory();
			String file_path = parentPath.getAbsolutePath()+("/")+DST_FOLDER_NAME+("/")+file_name;

			File file=new File(file_path);
			
			FileOutputStream fileW = new FileOutputStream(file.getCanonicalPath());
            fileW.write(content);  
            fileW.close();  

		} catch (Exception e) {
			// TODO: handle exception  
		}  
	}
	
	public static void deleteFile(File file) {
		if (file.exists()) {
			if (file.isFile()) {
				file.delete();
			} else if (file.isDirectory()) {
				File files[] = file.listFiles();
				for (int i = 0; i < files.length; i++) {
					deleteFile(files[i]);
				}
			}
			file.delete();
		} else {
			//
		}
	}

	public static void CopyAssets(Context context, String oldPath, String newPath) {
		try {
			Log.e(TAG, "CopyAssets:开始复制 " );
			String fileNames[] = context.getAssets().list(oldPath);// 获取assets目录下的所有文件及目录名
			if (fileNames.length > 0) {// 如果是目录
				Log.e(TAG, fileNames[0] );
				File file = new File(newPath);
				file.mkdirs();// 如果文件夹不存在，则递归
				for (String fileName : fileNames) {
					CopyAssets(context, oldPath + "/" + fileName, newPath + "/" + fileName);
				}
			} else {// 如果是文件
				InputStream is = context.getAssets().open(oldPath);
				FileOutputStream fos = new FileOutputStream(new File(newPath));
				byte[] buffer = new byte[1024];
				int byteCount = 0;
				while ((byteCount = is.read(buffer)) != -1) {// 循环从输入流读取
					// buffer字节
					fos.write(buffer, 0, byteCount);// 将读取的输入流写入到输出流
				}
				fos.flush();// 刷新缓冲区
				is.close();
				fos.close();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
}
