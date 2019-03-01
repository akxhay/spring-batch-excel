/*
 * Copyright 2006-2019 the original author or authors.
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

package org.springframework.batch.item.excel.poi;

import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.springframework.batch.item.excel.AbstractExcelItemReader;
import org.springframework.batch.item.excel.Sheet;

import java.io.InputStream;

/**
 * {@link org.springframework.batch.item.ItemReader} implementation which uses apache POI
 * to read an Excel file. It will read the file sheet for sheet and row for row. It is
 * based on the {@link org.springframework.batch.item.file.FlatFileItemReader}
 *
 * @param <T> the type
 * @author Marten Deinum
 * @since 0.5.0
 */
public class PoiItemReader<T> extends AbstractExcelItemReader<T> {

    private Workbook workbook;
    private boolean useDefaultCellType;

    @Override
    protected Sheet getSheet(final int sheet) {
        if(useDefaultCellType) {
            return new DefaulltMappingPoiSheet(this.workbook.getSheetAt(sheet));
        } else {
            return new PoiSheet(this.workbook.getSheetAt(sheet));
        }
    }
    
    public PoiItemReader(){
        super();
    }
    
    public PoiItemReader(boolean useDefaultCellType){
        super();
        this.useDefaultCellType = useDefaultCellType;
    }

    @Override
    protected int getNumberOfSheets() {
        return this.workbook.getNumberOfSheets();
    }

    @Override
    protected void doClose() throws Exception {
        super.doClose();
        if (this.workbook != null) {
            this.workbook.close();
        }

        this.workbook=null;
    }

    /**
     * Open the underlying file using the {@code WorkbookFactory}. We keep track of the used {@code InputStream} so that
     * it can be closed cleanly on the end of reading the file. This to be able to release the resources used by
     * Apache POI.
     *
     * @param inputStream the {@code InputStream} pointing to the Excel file.
     * @throws Exception is thrown for any errors.
     */
    @Override
    protected void openExcelFile(final InputStream inputStream) throws Exception {

        this.workbook = WorkbookFactory.create(inputStream);
        this.workbook.setMissingCellPolicy(Row.MissingCellPolicy.CREATE_NULL_AS_BLANK);
    }

}
