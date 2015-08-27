/* keloud Android Library is available under MIT license
 *   Copyright (C) 2015 keloud Inc.
 *   
 *   Permission is hereby granted, free of charge, to any person obtaining a copy
 *   of this software and associated documentation files (the "Software"), to deal
 *   in the Software without restriction, including without limitation the rights
 *   to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 *   copies of the Software, and to permit persons to whom the Software is
 *   furnished to do so, subject to the following conditions:
 *   
 *   The above copyright notice and this permission notice shall be included in
 *   all copies or substantial portions of the Software.
 *   
 *   THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, 
 *   EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF
 *   MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND 
 *   NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS 
 *   BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN 
 *   ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN 
 *   CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 *   THE SOFTWARE.
 *
 */

package ir.keloud.android.lib.resources.files;

import android.util.Log;

import java.io.File;
import java.io.FileDescriptor;
import java.io.FileInputStream;

import ir.keloud.android.lib.common.network.ServerNameIndicator;
import ir.keloud.android.lib.common.utils.Log_OC;

import org.apache.http.entity.FileEntity;

public class FileUtils {

	public static final String PATH_SEPARATOR = "/";


	public static String getParentPath(String remotePath) {
		String parentPath = new File(remotePath).getParent();
		parentPath = parentPath.endsWith(PATH_SEPARATOR) ? parentPath : parentPath + PATH_SEPARATOR;
		return parentPath;
	}
	
	/**
	 * Validate the fileName to detect if contains any forbidden character: / , \ , < , > , : , " , | , ? , *
	 * @param fileName
	 * @return
	 */
	public static boolean isValidName(String fileName) {
		boolean result = true;
		
		Log_OC.d("FileUtils", "fileName =======" + fileName);
		if (fileName.contains(PATH_SEPARATOR) ||
				fileName.contains("\\") || fileName.contains("<") || fileName.contains(">") ||
				fileName.contains(":") || fileName.contains("\"") || fileName.contains("|") || 
				fileName.contains("?") || fileName.contains("*")) {
			result = false;
		}
		return result;
	}
	
	/**
	 * Validate the path to detect if contains any forbidden character: \ , < , > , : , " , | , ? , *
	 * @param path
	 * @return
	 */
	public static boolean isValidPath(String path) {
		boolean result = true;


		Log_OC.d("FileUtils", "path ....... " + path);
		if (path.contains("\\") || path.contains("<") || path.contains(">") ||
				path.contains(":") || path.contains("\"") || path.contains("|") || 
				path.contains("?") || path.contains("*")) {
			result = false;
		}
		return result;
	}



    public static String fileName(String path) {

        String fileNameStr="" ;
        String[] separatedFileName;

 ///////////////////////// Farzad Test //////////////////////
 /*

        String test = "salam.txt.ddd.eee.hhh";
        separatedFileName = test.split("\\.");
        int k;
        for ( k = 0;k<separatedFileName.length;k++)
            Log.i("my test array", separatedFileName[k]);
        Log.i("my test array", Integer.toString(k));
*/

        try {
            separatedFileName = path.split("\\.");

            if (separatedFileName != null && separatedFileName.length >= 2) {
                for (int i = 0; i < separatedFileName.length - 1; i++) {
                    fileNameStr += separatedFileName[i];
                }
             }
            //fileNameStr = "NOname";
        } catch (Exception ex) {
            fileNameStr = "نامی موجود نیست";
        }
        return fileNameStr;
    }




    public static String fileExtension(String path) {

        String[] separatedFileName = null;
        String fileNameStr = "";
        String fileExtensionStr = "";
        try {

            separatedFileName = path.split("\\.");
            if ( separatedFileName.length > 1 )
                fileExtensionStr = separatedFileName[separatedFileName.length - 1];

        } catch (Exception ex) {
            fileExtensionStr = "فایل ناشناخته";
        }
        return fileExtensionStr;
    }


}
