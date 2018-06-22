package com.xiyu.schedulix.api.util;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SchedulixCMD {
	// 调用schedulix命令
	public static String etlConvert(String jobname, String documentId, String documentType) {
		String newStr = null;
		if (documentType.equals("0")) {
			newStr = jobname;
		} else {
			String str = jobname.substring(jobname.indexOf(".") + 1);
			newStr = jobname.substring(0, jobname.indexOf(".") + 1) + str.substring(0, str.indexOf(".")) + "_"
					+ documentType + str.substring(str.indexOf("."))+"_"+documentType;
		}

		String command = "echo \"submit " + newStr + "  with parameter=(ID='" + documentId + "');\"|sdmsh";
		String line = null;
		StringBuilder sb = new StringBuilder();
		Runtime runtime = Runtime.getRuntime();
		try {
			Process process = runtime.exec(new String[] { "/bin/sh", "-c", command });
			process.waitFor();
			BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(process.getInputStream()));
			while ((line = bufferedReader.readLine()) != null) {
				sb.append(line + "\n");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		String result = sb.toString();
		// System.out.println("etlConvert-return:" + result);
		String id = "";
		Pattern pattern = Pattern.compile("ID : (\\d*)\\n");
		Matcher matcher = pattern.matcher(result);
		if (matcher.find()) {
			id = matcher.group(1);
		} else {
			return null;
		}
		return id.trim();
	}

	/**
	 * 判断etl转换结果
	 */
	public static String etlConvertResult(String id) {
		String command = "echo \"list job " + id + " ;\"|sdmsh";
		// String command = "sh /home/ubuntu/schedulix/sdmsh_file/caiwu_wenben_2.sh " +
		// id;
		// String command = "sh /home/ubuntu/schedulix/sdmsh_file_01/caiwu_wenben_2.sh "
		// + id;
		// System.out.println("command:" + command);
		String line = null;
		StringBuilder sb = new StringBuilder();
		Runtime runtime = Runtime.getRuntime();
		try {
			Process process = runtime.exec(new String[] { "/bin/sh", "-c", command });
			process.waitFor();
			BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(process.getInputStream()));
			while ((line = bufferedReader.readLine()) != null) {
				// if(i==12) {
				sb.append(line + "\n");
				// }
				// i++;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		String result = sb.toString();
		// System.out.println("etlConvertResult-return:" + result);

		String regex = "\n(" + id + ".*)\n";
		Pattern pattern = Pattern.compile(regex);
		Matcher matcher = pattern.matcher(result);
		if (matcher.find()) {
			result = matcher.group(1);
		} else {
			return "keyNotFound";
		}
		String[] resultArr = result.split(" +");
		List<String> results = Arrays.asList(resultArr);
		// int i = 0;
		// for (String str : results) {
		// i++;
		// System.out.println("第" + i + "行：" + str);
		// }
		String flag = "false";
		// System.out.println("12:"+results.get(12));
		// System.out.println("16:"+results.get(16));
		if (results.get(12).equals("FINAL") && results.get(16).equals("SUCCESS")) {
			flag = "success";
		} else if (results.get(12).equals("FINISHED") && results.get(16).equals("FAILURE")) {
			flag = "error";
		} else if (results.get(12).equals("CANCELLED") && results.get(16).equals("FAILURE")) {
			flag = "cancelled";
		}
		return flag;
	}

	/**
	 * kill etl job
	 */
	public static boolean killEtlJob(String id) {
		String command = "echo \"alter job " + id + "with kill ;\"|sdmsh";
		// String command = "sh /home/ubuntu/schedulix/sdmsh_file/caiwu_wenben_2.sh " +
		// id;
		// String command = "sh /home/ubuntu/schedulix/sdmsh_file_01/caiwu_wenben_2.sh "
		// + id;
		// System.out.println("command:" + command);
		String line = null;
		StringBuilder sb = new StringBuilder();
		Runtime runtime = Runtime.getRuntime();
		try {
			Process process = runtime.exec(new String[] { "/bin/sh", "-c", command });
			process.waitFor();
			BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(process.getInputStream()));
			while ((line = bufferedReader.readLine()) != null) {
				// if(i==12) {
				sb.append(line + "\n");
				// }
				// i++;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		String result = sb.toString();
		// System.out.println("etlConvertResult-return:" + result);

		String regex = "Job altered";
		Pattern pattern = Pattern.compile(regex);
		Matcher matcher = pattern.matcher(result);
		if (matcher.find()) {
			return true;
		} else {
			return false;
		}

	}

	/**
	 * cancel error job
	 */
	public static boolean cancelErrorJob(String id) {
		String command = "echo \"alter job " + id + "with cancel ;\"|sdmsh";
		// String command = "sh /home/ubuntu/schedulix/sdmsh_file/caiwu_wenben_2.sh " +
		// id;
		// String command = "sh /home/ubuntu/schedulix/sdmsh_file_01/caiwu_wenben_2.sh "
		// + id;
		// System.out.println("command:" + command);
		String line = null;
		StringBuilder sb = new StringBuilder();
		Runtime runtime = Runtime.getRuntime();
		try {
			Process process = runtime.exec(new String[] { "/bin/sh", "-c", command });
			process.waitFor();
			BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(process.getInputStream()));
			while ((line = bufferedReader.readLine()) != null) {
				// if(i==12) {
				sb.append(line + "\n");
				// }
				// i++;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		String result = sb.toString();
		// System.out.println("etlConvertResult-return:" + result);

		String regex = "Job altered";
		Pattern pattern = Pattern.compile(regex);
		Matcher matcher = pattern.matcher(result);
		if (matcher.find()) {
			return true;
		} else {
			return false;
		}

	}
}
