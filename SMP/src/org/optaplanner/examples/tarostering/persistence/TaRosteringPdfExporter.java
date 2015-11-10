/*
 * Copyright 2010 JBoss Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.optaplanner.examples.tarostering.persistence;

import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.nio.file.FileSystems;
import java.util.List;
import org.optaplanner.examples.tarostering.domain.CourseAssignment;
import org.optaplanner.examples.tarostering.domain.CourseDay;
import org.optaplanner.examples.tarostering.domain.CourseType;
import org.optaplanner.examples.tarostering.domain.Ta;
import org.optaplanner.examples.tarostering.domain.TaRoster;

/**
 * Created by ahooper on 10/27/2015.
 */
public class TaRosteringPdfExporter {

    protected TaRoster taRoster;
    public final int PAGE_SIZE = 54;

    public TaRosteringPdfExporter(TaRoster taRoster) {
        this.taRoster = taRoster;
    }

    public void ExportToPdf(String fileName) {
        String scheduleName = taRoster.getCode();
        if (scheduleName.isEmpty()) scheduleName = "";

        Document document = new Document();
        int pageNumber = 1;
        try
        {
            PdfWriter writer = PdfWriter.getInstance(document, new FileOutputStream(fileName));
            document.open();

            PdfPTable table = AddPageHeader(document, pageNumber, scheduleName);
            Font cellFont = new Font(Font.FontFamily.HELVETICA, 8);

            List<CourseAssignment> list = taRoster.getCourseAssignmentList();

            for (int i = 0; i < list.size(); i++) {
                if (i > 0 && i % PAGE_SIZE == 0) {
                    pageNumber++;
                    document.add(table);
                    document.newPage();
                    table = AddPageHeader(document, pageNumber, scheduleName);
                }

                CourseAssignment ca = list.get(i);
                CourseType course = ca.getCourseType();
                CourseDay day = ca.getCourseDay();
                Ta ta = ca.getTa();

                AddTableRow(table, course, day, ta, cellFont);
            }

            document.add(table);

            document.close();
            writer.close();
        } catch (DocumentException e)
        {
            e.printStackTrace();
        } catch (FileNotFoundException e)
        {
            e.printStackTrace();
        }
    }

    public String ExportTaPdf(Ta ta, String path) {
        String scheduleName = taRoster.getCode();
        if (scheduleName.isEmpty()) scheduleName = "";

        int pageNumber = 1;
        try
        {
            Document document = new Document();
            String taName = ta.getName().replaceAll("\\s+","").replaceAll(",", "");
            File taFile = new File(path + FileSystems.getDefault().getSeparator() + taName + ".pdf");
            if (taFile.exists()) {
                taFile.delete();
            }
            PdfWriter writer = PdfWriter.getInstance(document, new FileOutputStream(taFile));
            document.open();

            PdfPTable table = AddPageHeader(document, pageNumber, scheduleName, ta.getName());
            Font cellFont = new Font(Font.FontFamily.HELVETICA, 8);

            List<CourseAssignment> list = taRoster.getCourseAssignmentList();

            for (CourseAssignment ca: list) {
                if (ca.getTa().getId() != ta.getId()) continue;

                CourseType course = ca.getCourseType();
                CourseDay day = ca.getCourseDay();

                AddTableRow(table, course, day, ta, cellFont);
            }

            document.add(table);

            document.close();
            writer.close();
            return taFile.getAbsolutePath();
        } catch (DocumentException | FileNotFoundException e)
        {
            throw new RuntimeException("Error exporting ta schedule");
        }

    }

    private static void AddTableRow(PdfPTable table, CourseType course, CourseDay day, Ta ta, Font font) {
        table.addCell(new Phrase(course.getCrn(), font)); //CRN
        table.addCell(new Phrase(course.getDepartment(), font)); //DEPT
        table.addCell(new Phrase(course.getCourseNumber(), font)); //CRS
        table.addCell(new Phrase(course.getSectionNumber(), font)); //SEC
        table.addCell(new Phrase(day.getDayOfWeek().getAbbrev(), font)); //DAY
        table.addCell(new Phrase(course.getStartTimeFormatted(), font)); //START
        table.addCell(new Phrase(course.getEndTimeFormatted(), font)); //END
        table.addCell(new Phrase(course.getBuilding(), font)); //BLDG
        table.addCell(new Phrase(course.getRoomNumber(), font)); //RM
        table.addCell(new Phrase(course.getCoordinatorName(), font)); //COORD
        table.addCell(new Phrase(ta.getName(), font)); //TA
        table.addCell(new Phrase(ta.getEmail(), font)); //EMAIL
    }

    private static PdfPTable AddPageHeader(Document document, int pageNumber, String scheduleName) throws DocumentException {
        return AddPageHeader(document, pageNumber, scheduleName, "");
    }

    private static PdfPTable AddPageHeader(Document document, int pageNumber, String scheduleName, String taName) throws DocumentException {
        Font headerFont = new Font(Font.FontFamily.HELVETICA, 8, Font.BOLD);
        Font titleFont = new Font(Font.FontFamily.HELVETICA, 10, Font.BOLD);

        PdfPTable table = new PdfPTable(12); // 3 columns.
        table.setWidthPercentage(100f);

        //Set Column widths
        float[] columnWidths = {
                2.5f, //CRN
                2.5f, //DEPT
                2f, //CRS
                1.8f, //SEC
                1.8f, //DAY
                2.5f, //START
                2.5f, //END
                2.2f, //BLDG
                2f, //RM
                6f, //COORD
                8f, //TA
                6f //EMAIL
        };
        table.setWidths(columnWidths);
        table.getDefaultCell().setPadding(3);
        table.getDefaultCell().setUseAscender(true);
        table.getDefaultCell().setUseDescender(true);
        table.getDefaultCell().setHorizontalAlignment(Element.ALIGN_CENTER);

        PdfPTable header = new PdfPTable(3);
        header.setWidths(new float[] { 10f, 20f, 10f});
        header.setWidthPercentage(100);
        header.getDefaultCell().setBorderColor(BaseColor.WHITE);
        header.addCell(new Paragraph(taName));
        header.getDefaultCell().setHorizontalAlignment(Element.ALIGN_CENTER);
        header.addCell(new Paragraph(scheduleName.toUpperCase() + " CHEMISTRY ASSISTANTS SCHEDULE", titleFont));
        header.getDefaultCell().setHorizontalAlignment(Element.ALIGN_RIGHT);
        header.getDefaultCell().setVerticalAlignment(Element.ALIGN_BOTTOM);
        header.addCell(new Paragraph("Page " + pageNumber, headerFont));

        document.add(header);
        document.add(new Paragraph("\n", headerFont));

        table.addCell(new Phrase("CRN", headerFont));
        table.addCell(new Phrase("DEPT", headerFont));
        table.addCell(new Phrase("CRS", headerFont));
        table.addCell(new Phrase("SEC", headerFont));
        table.addCell(new Phrase("DAY", headerFont));
        table.addCell(new Phrase("START", headerFont));
        table.addCell(new Phrase("END", headerFont));
        table.addCell(new Phrase("BLDG", headerFont));
        table.addCell(new Phrase("RM", headerFont));
        table.addCell(new Phrase("COORD", headerFont));
        table.addCell(new Phrase("TA", headerFont));
        table.addCell(new Phrase("EMAIL", headerFont));

        return table;
    }

}
