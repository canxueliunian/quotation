package com.ifast.common.utils;

/**
 * Created by lishuntao on 2018/11/26.
 */

import java.io.*;
import java.lang.reflect.Field;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.Map.Entry;

import com.alibaba.druid.support.logging.Log;
import com.alibaba.druid.support.logging.LogFactory;

import com.ifast.common.annotation.Excel;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.usermodel.Cell;
import jxl.write.Label;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.VerticalAlignment;


import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFFont;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.hssf.util.HSSFColor.HSSFColorPredefined;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;



public class ExeclUtil {
    static int sheetsize = 5000;
    // 红色标注样式
    public HSSFFont redHssfFont;
    public HSSFCellStyle cellStyle;

    // 样式文件
    public  HSSFWorkbook getWorkBook(){
        HSSFWorkbook workbook =new HSSFWorkbook();
        // 创建红色字体样式
        redHssfFont= workbook.createFont();
        redHssfFont.setColor(HSSFFont.COLOR_RED);
        // 创建cellStyle

        cellStyle.setAlignment(HorizontalAlignment.CENTER);
        cellStyle.setVerticalAlignment(VerticalAlignment.CENTER);
        cellStyle.setFont(redHssfFont);
        cellStyle.setFillForegroundColor(HSSFColorPredefined.YELLOW.getIndex());
        return  workbook;

    }

    /**
     * 表结构的内容及列字段由map传入
     */
    public static <T> void ListToHttp(List<T> data,
                                      Map<String, String> fields,
                                      String sheetName, String fileName,
                                      HttpServletResponse response) throws Exception {
        fileName = fileName + ".xls";
        response.setContentType("application/octet-stream");
        response.setHeader("Content-disposition", "attachment;filename=" + new String(fileName.getBytes(), "iso-8859-1"));//Excel文件名
        ServletOutputStream out = response.getOutputStream();
        List2ExeclByMap(data, out, fields, sheetName);
        out.close();
    }

    /**
     * 表结构的内容由属性list,传入, 对应列名称解析注解获得.
     */
    public static <T> void ListToHttp(List<T> data, List<String> fields, String sheetName, String fileName, HttpServletResponse response) throws Exception {

        fileName = fileName + ".xls";
        response.setContentType("application/octet-stream");
        response.setHeader("Content-disposition", "attachment;filename=" + new String(fileName.getBytes(), "iso-8859-1"));//Excel文件名
        ServletOutputStream out = response.getOutputStream();
        List2ExeclByList(data, out, fields, sheetName);
        out.close();
    }

    /**
     * 表结构内容及列名类中全部属性解析, 内容从@Excel中解析获得
     */
    public static <T> void ListToHttp(List<T> data, String sheetName, String fileName, HttpServletResponse response) throws Exception {

        fileName = fileName + ".xls";
        response.setContentType("application/octet-stream");
        response.setHeader("Content-disposition", "attachment;filename=" + new String(fileName.getBytes(), "iso-8859-1"));//Excel文件名
        ServletOutputStream out = response.getOutputStream();
        List2ExeclAll(data, out, sheetName);
        out.close();
    }

    /**
     * 全属性解析
     */
    private static <T> void List2ExeclAll(List<T> data, ServletOutputStream out, String sheetName) throws Exception {
        Map<String, String> fields = new HashMap<String, String>();
        HSSFWorkbook workbook = new HSSFWorkbook();
        // 如果导入数据为空，则抛出异常。
        if (data == null || data.size() == 0) {
            workbook.close();
            throw new Exception("导入的数据为空");
        }
        // 根据data计算有多少页sheet
        int pages = data.size() / sheetsize;
        if (data.size() % sheetsize > 0) {
            pages += 1;
        }
        Class<?> clazz = data.get(0).getClass();
        // 这两个内容分别从list中生成再进行转换
        List<String> egtitlesList = new LinkedList<>();
        List<String> cntitlesList = new LinkedList<>();
        // 得到所有定义字段
        Field[] allFields = clazz.getDeclaredFields();

        // 得到所有field并存放到一个list中.
        for (Field field : allFields) {
            if (field.isAnnotationPresent(Excel.class)) {
                egtitlesList.add(field.getName());
                Excel annotation = field.getAnnotation(Excel.class);
                String name = annotation.name();
                if (StringUtils.isNotEmpty(name)) {
                    cntitlesList.add(name);
                } else {
                    cntitlesList.add("");
                }

            }
        }


        // 提取表格的字段名（英文字段名是为了对照中文字段名的）
        String[] egtitles = egtitlesList.toArray(new String[egtitlesList.size()]);
        String[] cntitles = cntitlesList.toArray(new String[cntitlesList.size()]);
        Iterator<String> it = fields.keySet().iterator();
        int count = 0;
        while (it.hasNext()) {
            String egtitle = (String) it.next();
            String cntitle = fields.get(egtitle);
            egtitles[count] = egtitle;
            cntitles[count] = cntitle;
            count++;
        }
        // 添加数据
        for (int i = 0; i < pages; i++) {
            int rownum = 0;
            // 计算每页的起始数据和结束数据
            int startIndex = i * sheetsize;
            int endIndex = (i + 1) * sheetsize - 1 > data.size() ? data.size()
                    : (i + 1) * sheetsize - 1;
            // 创建每页，并创建第一行
            HSSFSheet sheet = workbook.createSheet(sheetName + i + "");
            HSSFRow row = sheet.createRow(rownum);

            // 在每页sheet的第一行中，添加字段名
            for (int f = 0; f < cntitles.length; f++) {
                HSSFCell cell = row.createCell(f);
                cell.setCellValue(cntitles[f]);

            }
            rownum++;
            // 将数据添加进表格
            for (int j = startIndex; j < endIndex; j++) {
                row = sheet.createRow(rownum);
                T item = (T) data.get(j);
                for (int h = 0; h < cntitles.length; h++) {
                    Field fd = item.getClass().getDeclaredField(egtitles[h]);
                    fd.setAccessible(true);
                    Object clumnValue = fd.get(item);


                    HSSFCell cell = row.createCell(h);
                    // 设置列中的值
                    Excel annotation = fd.getAnnotation(Excel.class);
                    if (annotation != null) {

                        String readConverterExp = annotation.readConverterExp();
                        String dateFormat = annotation.dateFormat();
                        // 进行设置转换关系
                        if (StringUtils.isNotEmpty(dateFormat)) {
                            cell.setCellValue(DateUtils.parseDateToStr(dateFormat, (Date) clumnValue));
                        } else if (StringUtils.isNotEmpty(readConverterExp)) {
                            cell.setCellValue(convertByExp(String.valueOf(clumnValue), readConverterExp));
                        } else {
                            cell.setCellType(Cell.CELL_TYPE_STRING);
                            // 如果数据存在就填入,不存在填入空格.
                            cell.setCellValue(clumnValue == null ? annotation.defaultValue() : clumnValue + annotation.suffix());
                        }
                    } else {
                        // 直接设置值
                        cell.setCellValue(String.valueOf(clumnValue));
                    }
                }
                rownum++;
            }

        }

        // 将创建好的数据写入输出流
        workbook.write(out);
        // 关闭workbook
        workbook.close();

    }


    /**
     * @param data   导入到excel中的数据
     * @param out    数据写入的文件
     * @param fields 需要注意的是这个方法中的map中：每一列对应的实体类的英文名为键，excel表格中每一列名为值
     * @throws Exception
     * @author Lyy
     */

    public static <T> void List2ExeclByMap(List<T> data, OutputStream out,
                                           Map<String, String> fields,
                                           String sheetName
    ) throws Exception {
        HSSFWorkbook workbook = new HSSFWorkbook();
        // 如果导入数据为空，则抛出异常。
        if (data == null || data.size() == 0) {
            workbook.close();
            throw new Exception("导入的数据为空");
        }
        // 根据data计算有多少页sheet
        int pages = data.size() / sheetsize;
        if (data.size() % sheetsize > 0) {
            pages += 1;
        }
        // 提取表格的字段名（英文字段名是为了对照中文字段名的）
        String[] egtitles = new String[fields.size()];
        String[] cntitles = new String[fields.size()];
        Iterator<String> it = fields.keySet().iterator();
        int count = 0;
        while (it.hasNext()) {
            String egtitle = (String) it.next();
            String cntitle = fields.get(egtitle);
            egtitles[count] = egtitle;
            cntitles[count] = cntitle;
            count++;
        }
        // 添加数据
        for (int i = 0; i < pages; i++) {
            int rownum = 0;
            // 计算每页的起始数据和结束数据
            int startIndex = i * sheetsize;
            int endIndex = (i + 1) * sheetsize - 1 > data.size() ? data.size()
                    : (i + 1) * sheetsize - 1;
            // 创建每页，并创建第一行
            HSSFSheet sheet = workbook.createSheet(sheetName + i + "");
            HSSFRow row = sheet.createRow(rownum);

            // 在每页sheet的第一行中，添加字段名
            for (int f = 0; f < cntitles.length; f++) {
                HSSFCell cell = row.createCell(f);
                cell.setCellValue(cntitles[f]);
                // 生成表格的名称
//                T item = data.get(f);

//                Field fd = item.getClass().getDeclaredField(egtitles[f]);

//                Excel annotation = fd.getAnnotation(Excel.class);
//                String name = annotation.name();
//                cell.setCellValue(name);
            }
            rownum++;
            // 将数据添加进表格
            for (int j = startIndex; j < endIndex; j++) {
                row = sheet.createRow(rownum);
                T item = data.get(j);
                for (int h = 0; h < cntitles.length; h++) {
                    Field fd = item.getClass().getDeclaredField(egtitles[h]);
                    fd.setAccessible(true);
                    Object clumnValue = fd.get(item);


                    HSSFCell cell = row.createCell(h);
                    // 设置列中的值
                    Excel annotation = fd.getAnnotation(Excel.class);
                    if (annotation != null) {

                        String readConverterExp = annotation.readConverterExp();
                        String dateFormat = annotation.dateFormat();
                        // 进行设置转换关系
                        if (StringUtils.isNotEmpty(dateFormat)) {
                            cell.setCellValue(DateUtils.parseDateToStr(dateFormat, (Date) clumnValue));
                        } else if (StringUtils.isNotEmpty(readConverterExp)) {
                            cell.setCellValue(convertByExp(String.valueOf(clumnValue), readConverterExp));
                        } else {
                            cell.setCellType(Cell.CELL_TYPE_STRING);
                            // 如果数据存在就填入,不存在填入空格.
                            cell.setCellValue(clumnValue == null ? annotation.defaultValue() : clumnValue + annotation.suffix());
                        }
                    } else {
                        // 直接设置值
                        cell.setCellValue(String.valueOf(clumnValue));
                    }
                }
                rownum++;
            }

        }

        // 将创建好的数据写入输出流
        workbook.write(out);
        // 关闭workbook
        workbook.close();
    }

    /**
     * @param data   导入到excel中的数据
     * @param out    数据写入的文f件
     * @param fields 需要注意的是这个方法中的map中：每一列对应的实体类的英文名为键，excel表格中每一列名为值
     * @throws Exception
     * @author Lyy
     * todo 之后将传入map条件或者是list条件的方法进行统一的设置
     */

    public static <T> void List2ExeclByList(List<T> data, OutputStream out,
                                            List<String> fields,
                                            String sheetName
    ) throws Exception {
        HSSFWorkbook workbook = new HSSFWorkbook();
        // 如果导入数据为空，则抛出异常。
        if (data == null || data.size() == 0) {
            workbook.close();
            throw new Exception("导入的数据为空");
        }
        // 根据data计算有多少页sheet
        int pages = data.size() / sheetsize;
        if (data.size() % sheetsize > 0) {
            pages += 1;
        }
        // 提取表格的字段名（英文字段名是为了对照中文字段名的）
        String[] egtitles = new String[fields.size()];
        String[] cntitles = new String[fields.size()];
        Iterator<String> it = fields.iterator();
        int count = 0;
        while (it.hasNext()) {
            String egtitle = (String) it.next();
            egtitles[count] = egtitle;
            count++;
        }
        // 添加数据
        for (int i = 0; i < pages; i++) {
            int rownum = 0;
            // 计算每页的起始数据和结束数据
            int startIndex = i * sheetsize;
            int endIndex = (i + 1) * sheetsize - 1 > data.size() ? data.size()
                    : (i + 1) * sheetsize - 1;
            // 创建每页，并创建第一行
            HSSFSheet sheet = workbook.createSheet(sheetName + i + "");
            HSSFRow row = sheet.createRow(rownum);

            // 在每页sheet的第一行中，添加字段名
            for (int f = 0; f < cntitles.length; f++) {
                HSSFCell cell = row.createCell(f);
                // 表格的列名称从注解中进行获取
                T item = data.get(f);
                Field fd = item.getClass().getDeclaredField(egtitles[f]);
                Excel annotation = fd.getAnnotation(Excel.class);
                String name = annotation.name();
                cell.setCellValue(name);
            }
            rownum++;
            // 将数据添加进表格
            for (int j = startIndex; j < endIndex; j++) {
                row = sheet.createRow(rownum);
                T item = data.get(j);
                for (int h = 0; h < cntitles.length; h++) {
                    Field fd = item.getClass().getDeclaredField(egtitles[h]);
                    fd.setAccessible(true);
                    Object o = fd.get(item);

                    // 使用注解中的值进行转换
                    HSSFCell cell = row.createCell(h);
                    // 设置列中的值
                    Excel annotation = fd.getAnnotation(Excel.class);
                    String readConverterExp = annotation.readConverterExp();
                    String dateFormat = annotation.dateFormat();
                    // 进行设置转换关系
                    if (StringUtils.isNotEmpty(dateFormat)) {
                        cell.setCellValue(DateUtils.parseDateToStr(dateFormat, (Date) o));
                    } else if (StringUtils.isNotEmpty(readConverterExp)) {
                        cell.setCellValue(convertByExp(String.valueOf(o), readConverterExp));
                    } else {
                        cell.setCellType(Cell.CELL_TYPE_STRING);
                        // 如果数据存在就填入,不存在填入空格.
                        cell.setCellValue(o == null ? annotation.defaultValue() : o + annotation.suffix());
                    }
                }
                rownum++;
            }
        }
        // 将创建好的数据写入输出流
        workbook.write(out);
        // 关闭workbook
        workbook.close();
    }


    /**
     * 　　* @author Lyy
     * 　　* @param entityClass excel中每一行数据的实体类
     * 　　* @param in          excel文件
     * 　　* @param fields     字段名字
     * 　　*             需要注意的是这个方法中的map中：
     * 　　*             excel表格中每一列名为键，每一列对应的实体类的英文名为值
     * 　　 * @throws Exception
     */
    public static <T> List<T> ExecltoList(InputStream in, Class<T> entityClass,
                                          Map<String, String> fields) throws Exception {

        List<T> resultList = new ArrayList<T>();

        HSSFWorkbook workbook = new HSSFWorkbook(in);

        // excel中字段的中英文名字数组
        String[] egtitles = new String[fields.size()];
        String[] cntitles = new String[fields.size()];
        Iterator<String> it = fields.keySet().iterator();
        int count = 0;
        while (it.hasNext()) {
            String cntitle = (String) it.next();
            String egtitle = fields.get(cntitle);
            egtitles[count] = egtitle;
            cntitles[count] = cntitle;
            count++;
        }

        // 得到excel中sheet总数
        int sheetcount = workbook.getNumberOfSheets();

        if (sheetcount == 0) {
            workbook.close();
            throw new Exception("Excel文件中没有任何数据");
        }

        // 数据的导出
        for (int i = 0; i < sheetcount; i++) {
            HSSFSheet sheet = workbook.getSheetAt(i);
            if (sheet == null) {
                continue;
            }
            // 每页中的第一行为标题行，对标题行的特殊处理
            HSSFRow firstRow = sheet.getRow(0);
            int celllength = firstRow.getLastCellNum();

            String[] excelFieldNames = new String[celllength];
            LinkedHashMap<String, Integer> colMap = new LinkedHashMap<String, Integer>();

            // 获取Excel中的列名
            for (int f = 0; f < celllength; f++) {
                HSSFCell cell = firstRow.getCell(f);
                excelFieldNames[f] = cell.getStringCellValue().trim();
                // 将列名和列号放入Map中,这样通过列名就可以拿到列号
                for (int g = 0; g < excelFieldNames.length; g++) {
                    colMap.put(excelFieldNames[g], g);
                }
            }
            // 由于数组是根据长度创建的，所以值是空值，这里对列名map做了去空键的处理
            colMap.remove(null);
            // 判断需要的字段在Excel中是否都存在
            // 需要注意的是这个方法中的map中：中文名为键，英文名为值
            boolean isExist = true;
            List<String> excelFieldList = Arrays.asList(excelFieldNames);
            for (String cnName : fields.keySet()) {
                if (!excelFieldList.contains(cnName)) {
                    isExist = false;
                    break;
                }
            }
            // 如果有列名不存在，则抛出异常，提示错误
            if (!isExist) {
                workbook.close();
                throw new Exception("Excel中缺少必要的字段，或字段名称有误");
            }
            // 将sheet转换为list
            for (int j = 1; j <= sheet.getLastRowNum(); j++) {
                HSSFRow row = sheet.getRow(j);
                // 根据泛型创建实体类
                T entity = entityClass.newInstance();
                // 给对象中的字段赋值
                for (Entry<String, String> entry : fields.entrySet()) {
                    // 获取中文字段名
                    String cnNormalName = entry.getKey();
                    // 获取英文字段名
                    String enNormalName = entry.getValue();
                    // 根据中文字段名获取列号
                    int col = colMap.get(cnNormalName);
                    // 获取当前单元格中的内容
                    String content = row.getCell(col).toString().trim();
                    // 给对象赋值
                    setFieldValueByName(enNormalName, content, entity);
                }
                resultList.add(entity);
            }
        }
        workbook.close();
        return resultList;
    }

    /**
     * @param fieldName  字段名
     * @param fieldValue 字段值
     * @param o          对象
     * @MethodName : setFieldValueByName
     * @Description : 根据字段名给对象的字段赋值
     */
    private static void setFieldValueByName(String fieldName,
                                            Object fieldValue, Object o) throws Exception {

        Field field = getFieldByName(fieldName, o.getClass());
        if (field != null) {
            field.setAccessible(true);
            // 获取字段类型
            Class<?> fieldType = field.getType();

            // 根据字段类型给字段赋值
            if (String.class == fieldType) {
                field.set(o, String.valueOf(fieldValue));
            } else if ((Integer.TYPE == fieldType)
                    || (Integer.class == fieldType)) {
                field.set(o, Integer.parseInt(fieldValue.toString()));
            } else if ((Long.TYPE == fieldType) || (Long.class == fieldType)) {
                field.set(o, Long.valueOf(fieldValue.toString()));
            } else if ((Float.TYPE == fieldType) || (Float.class == fieldType)) {
                field.set(o, Float.valueOf(fieldValue.toString()));
            } else if ((Short.TYPE == fieldType) || (Short.class == fieldType)) {
                field.set(o, Short.valueOf(fieldValue.toString()));
            } else if ((Double.TYPE == fieldType)
                    || (Double.class == fieldType)) {
                field.set(o, Double.valueOf(fieldValue.toString()));
            } else if (Character.TYPE == fieldType) {
                if ((fieldValue != null)
                        && (fieldValue.toString().length() > 0)) {
                    field.set(o,
                            Character.valueOf(fieldValue.toString().charAt(0)));
                }
            } else if (Date.class == fieldType) {
                field.set(o, new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
                        .parse(fieldValue.toString()));
            } else {
                field.set(o, fieldValue);
            }
        } else {
            throw new Exception(o.getClass().getSimpleName() + "类不存在字段名 "
                    + fieldName);
        }
    }

    /**
     * @param fieldName 字段名
     * @param clazz     包含该字段的类
     * @return 字段
     * @MethodName : getFieldByName
     * @Description : 根据字段名获取字段
     */
    private static Field getFieldByName(String fieldName, Class<?> clazz) {
        // 拿到本类的所有字段
        Field[] selfFields = clazz.getDeclaredFields();

        // 如果本类中存在该字段，则返回
        for (Field field : selfFields) {
            if (field.getName().equals(fieldName)) {
                return field;
            }
        }

        // 否则，查看父类中是否存在此字段，如果有则返回
        Class<?> superClazz = clazz.getSuperclass();
        if (superClazz != null && superClazz != Object.class) {
            return getFieldByName(fieldName, superClazz);
        }
        // 如果本类和父类都没有，则返回空
        return null;
    }

    private final static Log logger = LogFactory.getLog(ExeclUtil.class);

    public static void mergeExcel(String outputFileName, String... inputFileNameArray) {
//        if (inputFileNameArray.length == 1) {
//            logger.info("至少需要两个文件才能合并,请验证!!!");
//            return;
//        }
//        try {
//            WritableWorkbook outputExcel = Workbook.createWorkbook(new File(outputFileName));
//            int index = 0;
//            for (String fileName : inputFileNameArray) {
//                // 创建excel文件的工作簿对象book
//                Workbook inputExcel = Workbook.getWorkbook(new FileInputStream(fileName));
//                // 获取excel文件工作簿的工作表数量sheets
//                Sheet[] sheets = inputExcel.getSheets();
//                for (Sheet sheet : sheets) {
//                    WritableSheet writableSheet = outputExcel.createSheet(sheet.getName(), index);
//                    copy(sheet, writableSheet);
//                    index++;
//                }
//            }
///** **********将以上缓存中的内容写到EXCEL文件中******** */
//            outputExcel.write();
///** *********关闭文件************* */
//            outputExcel.close();
//        } catch (Exception e) {
//            StringWriter sw = new StringWriter();
//            PrintWriter pw = new PrintWriter(sw);
//            e.printStackTrace(pw);
//            logger.error(sw.toString());
//        }
    }

    private static void copy(Sheet formSheet, WritableSheet toWritableSheet) {
//// 行数
//        int rows = formSheet.getRows();
//// 列数
//        int columns = formSheet.getColumns();
//        for (int rowIndex = 0; rowIndex < rows; rowIndex++) {
//            for (int columnIndex = 0; columnIndex < columns; columnIndex++) {
//// 获取当前工作表.row_index,column_index单元格的cell对象
//                jxl.Cell cell = formSheet.getCell(columnIndex, rowIndex);
//                try {
//                    toWritableSheet.addCell(new Label(rowIndex, columnIndex, cell.getContents(), cell.getCellFormat()));
//                } catch (Exception e) {
//                    StringWriter sw = new StringWriter();
//                    PrintWriter pw = new PrintWriter(sw);
//                    e.printStackTrace(pw);
//                    logger.error(sw.toString());
//                }
//            }
//        }
    }

    /**
     * 解析导出值 0=男,1=女,2=未知
     *
     * @param propertyValue 参数值
     * @param converterExp  翻译注解
     * @return 解析后值
     * @throws Exception
     */
    public static String convertByExp(String propertyValue, String converterExp) throws Exception {
        try {
            String[] convertSource = converterExp.split(",");
            for (String item : convertSource) {
                String[] itemArray = item.split("=");
                if (itemArray[0].equals(propertyValue)) {
                    return itemArray[1];
                }
            }
        } catch (Exception e) {
            throw e;
        }
        return propertyValue;
    }

}