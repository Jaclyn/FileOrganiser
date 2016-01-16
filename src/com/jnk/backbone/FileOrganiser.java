package com.jnk.backbone;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

/**
 * 
 */

/**
 * @author XinMan
 *
 */
public class FileOrganiser {
	
	private String targetFolderPath = "D:\\Desktop";
	private Map<String, String> fileTypeSubFolderMap = new HashMap<String, String>();
	
	private void renameFileByExtension(File targetFolder, String extension){
		File[] subFiles = targetFolder.listFiles();
		for (File subFile: subFiles){
			int extIndex = subFile.getName().lastIndexOf(".")+1;
			String realExt = subFile.getName().substring(extIndex);
			if(realExt.toUpperCase() != extension.toUpperCase()){
//				String newFileName = subFile.getPath().replace("."+extension, "_").concat("."+extension);
				String newFileName = subFile.getPath().replace("..", ".");
				File newFile = new File(newFileName);
				boolean moveFlag = subFile.renameTo(newFile);
				if(!moveFlag){
					System.out.println("ERROR: "+subFile.getPath()+" failed to rename to "+newFileName);
					break;
				}else{
					System.out.println("INFO: "+subFile.getPath()+" is successfully renamed to "+newFileName);
				}
			}
		}
	}
	
	private boolean moveFile(File fileToMove, String newFilePath){
		File newFile = new File(newFilePath);
		if(newFile.exists()){
			String[] fileInfo = newFile.getAbsolutePath().split("\\.");
			newFilePath = fileInfo[0] + System.currentTimeMillis() + "." + fileInfo[1];
			newFile = new File(newFilePath);
		}
		boolean moveFlag = fileToMove.renameTo(newFile);
		if(!moveFlag){
			System.out.println("ERROR: "+fileToMove.getPath()+" failed to move to "+newFilePath);
		}else{
			System.out.println("INFO: "+fileToMove.getPath()+" is successfully move to "+newFilePath);
		}
		return moveFlag;
	}
	
	private boolean deleteFolderIfEmpty(File folderToCheck){
		boolean emptyFlag = false;
		boolean deleteFlag = false;
		File[] subFolderFiles = folderToCheck.listFiles();
		if(subFolderFiles.length < 1){
			emptyFlag = true;
		}
		if(emptyFlag){
			deleteFlag = folderToCheck.delete();
			if(!deleteFlag){
				System.out.println("ERROR: "+folderToCheck.getPath()+" failed to delete");
			}else{
				System.out.println("INFO: "+folderToCheck.getPath()+" is empty and deleted.");
			}
		}
		return emptyFlag == deleteFlag;
	}
	
	private boolean createNewFolder(String newFolderPath){
		File newFolder = new File(newFolderPath);
		if(!newFolder.exists() && !newFolder.mkdir()){
			System.out.println("ERROR: "+newFolder.getPath()+" failed to create");
			return false;
		}else{
			System.out.println("INFO: "+newFolder.getPath()+" is successfully created.");
			return true;
		}
	}
	
	private boolean extractSubFolderFilesIntoTargetFolder(File subFolder){
		if(!deleteFolderIfEmpty(subFolder)){
			return false;
		}
		
		File[] subFolderFiles = subFolder.listFiles();
		for(File subItem: subFolderFiles){
			if(subItem.isDirectory()){
				extractSubFolderFilesIntoTargetFolder(subItem);
			}else{
				String newFilePath = targetFolderPath+"\\"+subItem.getName();
				if(!moveFile(subItem, newFilePath)){
					return false;
				}
			}
		}
		
		if(!deleteFolderIfEmpty(subFolder)){
			return false;
		}
		
		return true;
	}
	
	private boolean locateFileIntoTargetFolder(File targetFolder){
		File[] subFiles = targetFolder.listFiles();
		for(File subItem: subFiles){
			if(subItem.isDirectory()){
				if(!extractSubFolderFilesIntoTargetFolder(subItem)){
					return false;
				}
			}
		}
		
		return true;
	}
	
	private boolean distributeFileIntoFileTypeFolder(File targetFolder, String extension, String folderNameToCreate){
		fileTypeSubFolderMap.clear();
		File[] subFiles = targetFolder.listFiles();
		for(File subItem: subFiles){
			if(subItem.getName().indexOf(".") == 0){
				continue;
			}
			int startIndex = subItem.getName().lastIndexOf(".")+1;
			String fileType = subItem.getName().substring(startIndex);
			if(fileType.length()>4){
				int endIndex = startIndex + 3;
				fileType = subItem.getName().substring(startIndex, endIndex);
			}
			fileType = fileType.toUpperCase();
			extension = extension.toUpperCase();
			
			if(!extension.equals("*") && extension.indexOf(fileType) < 0){
				continue;
			}
			if(folderNameToCreate.equals("*")){
				folderNameToCreate = fileType;
			}else if(folderNameToCreate.indexOf("\\")> -1){
				String[] pathSegment = folderNameToCreate.split(Pattern.quote(System.getProperty("file.separator")));
				for(int i=0; i < pathSegment.length; i++){
					if(i == pathSegment.length -1){
						break;
					}
					String newFolderPath = targetFolderPath + "\\" + pathSegment[i].toUpperCase();
					if(!createNewFolder(newFolderPath)){
						return false;
					}
				}
			}
			
			System.out.println("DEBUG: fileType "+fileType);
			if(!fileTypeSubFolderMap.containsKey(fileType)){
				String subFolderPath = targetFolderPath+"\\"+folderNameToCreate.toUpperCase();
				fileTypeSubFolderMap.put(fileType, subFolderPath);
				if(!createNewFolder(subFolderPath)){
					return false;
				}
			}
			String newFilePath = fileTypeSubFolderMap.get(fileType)+"\\"+subItem.getName();
			if(!moveFile(subItem, newFilePath)){
				return false;
			}
			
		}
		return true;
	}
	
	private boolean distributeFileIntoFileTypeFolder(File targetFolder, String extension){
		return distributeFileIntoFileTypeFolder(targetFolder, extension, extension);
	}
	
	private boolean distributeFileIntoFileTypeFolder(File targetFolder){
		return distributeFileIntoFileTypeFolder(targetFolder, "*","*");
	}
	
	private boolean distributeFileIntoDateFolder(File targetFolder){
		File[] subFiles = targetFolder.listFiles();
		for(File subItem: subFiles){
			SimpleDateFormat df = new SimpleDateFormat();
			Date lastModifiedDate = new Date(subItem.lastModified());
			df.applyPattern("yyyy");
			String yearFolderPath = targetFolder.getPath() + "\\" + df.format(lastModifiedDate);
			if(!createNewFolder(yearFolderPath)){
				return false;
			}
			
			df.applyPattern("MMM");
			String monthFolderPath = yearFolderPath + "\\" + df.format(lastModifiedDate);
			if(!createNewFolder(monthFolderPath)){
				return false;
			}
			
			String newFilePath = monthFolderPath + "\\" + subItem.getName();
			if(!moveFile(subItem, newFilePath)){
				return false;
			}
			
		}
		return true;
	}
	
	public void run(){
		File targetFolder = new File(targetFolderPath);

		if(!targetFolder.exists()){
			System.out.println("ERROR: "+targetFolderPath+" is not exists.");
			return;
		}
		if(!targetFolder.isDirectory()){
			System.out.println("ERROR: "+targetFolderPath+" is not a directory.");
			return;
		}
		
		locateFileIntoTargetFolder(targetFolder);
//		distributeFileIntoFileTypeFolder(targetFolder);
		distributeFileIntoFileTypeFolder(targetFolder, "mp4");
		distributeFileIntoFileTypeFolder(targetFolder, "zip", "chuan\\zip");
		distributeFileIntoFileTypeFolder(targetFolder, "pdf", "chuan\\Geran_Plan");
		distributeFileIntoFileTypeFolder(targetFolder, "bmp,jpg,png", "chuan\\Google_Map");
//		renameFileByExtension(targetFolder, "pdf");
		distributeFileIntoDateFolder(new File("D:\\Desktop\\CHUAN\\GERAN_PLAN"));
	}

}
