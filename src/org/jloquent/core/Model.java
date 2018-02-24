/*
 * The MIT License
 *
 * Copyright 2018 Derick Felix.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package org.jloquent.core;

import java.lang.reflect.Method;
import java.util.List;
import org.jloquent.database.Database;
import org.jloquent.util.ObjectUtility;

/**
 *
 * @author derickfelix
 * @date Feb 24, 2018
 */
public abstract class Model {

    public void create() {
        String className = this.getClass().getName();
        Method[] mt = this.getClass().getDeclaredMethods();
        List<Field> fields = ObjectUtility.getFields(mt, this);
        
        String sql = "INSERT INTO `" + ObjectUtility.getTableName(className) + "` (";
        for (int i = 0; i < fields.size(); i++) {
            Field field = fields.get(i);
            
            sql += ("`" + field.getName() + "`");
            
            if ((i + 1) != fields.size()) {
                sql += ", ";
            }
        }
        
        sql += ") VALUES (";
        for (int i = 0; i < fields.size(); i++) {
            Field field = fields.get(i);
            
            sql += ("'" + field.getValue()+ "'");
            
            if ((i + 1) != fields.size()) {
                sql += ", ";
            }
        }
        
        sql += ")";
        
        Database.execute(sql);
    }

    public void update() {

    }

    public void delete() {

    }

    public Model find(int id) {
        return this;
    }

    public void all() {

    }
}
