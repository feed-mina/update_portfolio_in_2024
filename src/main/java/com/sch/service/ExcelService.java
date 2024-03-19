package com.sch.service;

import java.awt.Color;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFColor;
import org.apache.poi.xssf.usermodel.XSSFDataFormat;
import org.apache.poi.xssf.usermodel.XSSFFont;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service("statisticsService")
public class ExcelService {
	private Logger log = LoggerFactory.getLogger(this.getClass());

	public void exportDataToExcel(String title, List<Map<String, Object>> datas, HttpServletResponse response)
			throws Exception {
		exportDataToExcel(title, datas, false, response);
	}

	@SuppressWarnings("unchecked")
	public void exportDataToExcel(String title, List<Map<String, Object>> datas, boolean isAutoIncrement,
			HttpServletResponse response) throws Exception {

		String fileName = URLEncoder.encode(title, "UTF-8").replaceAll("\\+", "%20");
		String nowDate = new SimpleDateFormat("yyyy-MM-dd").format(new Date());
		String headerValue = String.format("attachment; filename=%s_%s.xlsx", nowDate, fileName);
		String headerKey = "Content-Disposition";
		response.setHeader(headerKey, headerValue);
		response.setContentType("application/octet-stream");

		XSSFWorkbook workbook = new XSSFWorkbook();
		XSSFSheet sheet = workbook.createSheet(title);
		XSSFRow row = null;
		int rowIdx = 0;
		int colIdx = 0;

		XSSFFont headerFont = workbook.createFont();
		headerFont.setFontName("Arial");
		headerFont.setBold(true);
		headerFont.setColor(new XSSFColor(new Color(110, 112, 126)));

		XSSFCellStyle subTitleCellStyle = workbook.createCellStyle();
		subTitleCellStyle.setFont(headerFont);
		subTitleCellStyle.setAlignment(CellStyle.ALIGN_CENTER);
		subTitleCellStyle.setVerticalAlignment(CellStyle.VERTICAL_CENTER);
		subTitleCellStyle.setBorderTop(BorderStyle.THIN);
		subTitleCellStyle.setBorderRight(BorderStyle.THIN);
		subTitleCellStyle.setBorderBottom(BorderStyle.THIN);
		subTitleCellStyle.setBorderLeft(BorderStyle.THIN);
		subTitleCellStyle.setFillForegroundColor(new XSSFColor(new Color(255, 255, 255)));
		subTitleCellStyle.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);

		XSSFCellStyle headerCellStyle = workbook.createCellStyle();
		headerCellStyle.setFont(headerFont);
		headerCellStyle.setAlignment(CellStyle.ALIGN_CENTER);
		headerCellStyle.setVerticalAlignment(CellStyle.VERTICAL_CENTER);
		headerCellStyle.setBorderTop(BorderStyle.THIN);
		headerCellStyle.setBorderRight(BorderStyle.THIN);
		headerCellStyle.setBorderBottom(BorderStyle.THIN);
		headerCellStyle.setBorderLeft(BorderStyle.THIN);
		headerCellStyle.setFillForegroundColor(new XSSFColor(new Color(234, 236, 244)));
		headerCellStyle.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);

		XSSFCellStyle stringCellStyle = workbook.createCellStyle();
		stringCellStyle.setFont(headerFont);
		stringCellStyle.setAlignment(CellStyle.ALIGN_CENTER);
		stringCellStyle.setVerticalAlignment(CellStyle.VERTICAL_CENTER);
		stringCellStyle.setBorderTop(BorderStyle.THIN);
		stringCellStyle.setBorderRight(BorderStyle.THIN);
		stringCellStyle.setBorderBottom(BorderStyle.THIN);
		stringCellStyle.setBorderLeft(BorderStyle.THIN);
		stringCellStyle.setFillForegroundColor(new XSSFColor(new Color(255, 255, 255)));
		stringCellStyle.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);

		XSSFCellStyle intCellStyle = workbook.createCellStyle();
		XSSFDataFormat intDataFormat = workbook.createDataFormat();
		intCellStyle.setFont(headerFont);
		intCellStyle.setAlignment(CellStyle.ALIGN_CENTER);
		intCellStyle.setVerticalAlignment(CellStyle.VERTICAL_CENTER);
		intCellStyle.setBorderTop(BorderStyle.THIN);
		intCellStyle.setBorderRight(BorderStyle.THIN);
		intCellStyle.setBorderBottom(BorderStyle.THIN);
		intCellStyle.setBorderLeft(BorderStyle.THIN);
		intCellStyle.setFillForegroundColor(new XSSFColor(new Color(255, 255, 255)));
		intCellStyle.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);
		intCellStyle.setDataFormat(intDataFormat.getFormat("#,##0"));

		XSSFCellStyle percentageCellStyle = workbook.createCellStyle();
		XSSFDataFormat percentageDataFormat = workbook.createDataFormat();
		percentageCellStyle.setFont(headerFont);
		percentageCellStyle.setAlignment(CellStyle.ALIGN_CENTER);
		percentageCellStyle.setVerticalAlignment(CellStyle.VERTICAL_CENTER);
		percentageCellStyle.setBorderTop(BorderStyle.THIN);
		percentageCellStyle.setBorderRight(BorderStyle.THIN);
		percentageCellStyle.setBorderBottom(BorderStyle.THIN);
		percentageCellStyle.setBorderLeft(BorderStyle.THIN);
		percentageCellStyle.setFillForegroundColor(new XSSFColor(new Color(255, 255, 255)));
		percentageCellStyle.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);
		percentageCellStyle.setDataFormat(percentageDataFormat.getFormat("0\"%\""));

		for (Map<String, Object> dataMap : datas) {
			String[] headers = (String[]) dataMap.get("headers");
			List<HashMap<String, Object>> list = (List<HashMap<String, Object>>) dataMap.get("list");

			// append subTitle
			if (dataMap.get("subTitle") != null) {
				row = sheet.createRow(rowIdx++);
				createStringCell(row, 0, dataMap.get("subTitle").toString(), subTitleCellStyle);
			}

			// append header
			XSSFRow headerRow = sheet.createRow(rowIdx++);
			for (int j = 0; j < headers.length; j++) {
				createStringCell(headerRow, j, headers[j], headerCellStyle);
			}

			// append data
			for (int j = 0; j < list.size(); j++) {
				row = sheet.createRow(rowIdx++);
				colIdx = 0;

				if (isAutoIncrement) {
					createIntCell(row, colIdx++, list.size() - j, intCellStyle);
				}

				for (Object data : list.get(j).values()) {
					if (data == null) {
						createStringCell(row, colIdx++, "", stringCellStyle);
					} else if (StringUtils.isNumeric(data.toString())) {
						createIntCell(row, colIdx++, Integer.parseInt(data.toString()), intCellStyle);
					} else if (data.toString().matches("^((100)|(\\d{1,2}(.\\d*)?))%$")) {
						createIntCell(row, colIdx++, Integer.parseInt(data.toString().split("%")[0]),
								percentageCellStyle);
					} else {
						createStringCell(row, colIdx++, data.toString(), stringCellStyle);
					}
				}
			}

			// auto size
			for (int j = 0; j < headers.length; j++) {
				sheet.autoSizeColumn(j);
				sheet.setColumnWidth(j, sheet.getColumnWidth(j) + 200);
			}

			// prepare next list..
			rowIdx++;
		}

		workbook.write(response.getOutputStream());
		workbook.close();
	}

	private void createStringCell(Row row, int colIdx, String value, CellStyle cellStyle) {
		Cell cell = row.createCell(colIdx, Cell.CELL_TYPE_STRING);
		cell.setCellValue(value);
		cell.setCellStyle(cellStyle);
	}

	private void createIntCell(Row row, int colIdx, int value, CellStyle cellStyle) {
		Cell cell = row.createCell(colIdx, Cell.CELL_TYPE_NUMERIC);
		cell.setCellValue(value);
		cell.setCellStyle(cellStyle);
	}

}
