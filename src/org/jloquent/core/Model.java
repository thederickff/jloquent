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

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;
import org.jloquent.database.Database;
import org.jloquent.util.ObjectUtility;

/**
 *
 * @author derickfelix
 * @date Feb 24, 2018
 */
public abstract class Model {

    /**
     * Creates a new entity into a table with the same name of a model child but
     * in plural, e.g. a model <code>class Person extends Model</code> will have
     * all of its fields persisted into a table called <code>persons</code>.
     */
    public void save() {
        Method[] mt = this.getClass().getDeclaredMethods();
        List<Field> fields = ObjectUtility.getFields(mt, this);

        String sql = "INSERT INTO `" + ObjectUtility.tableOf(this) + "` (";
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

            sql += ("'" + field.getValue() + "'");

            if ((i + 1) != fields.size()) {
                sql += ", ";
            }
        }

        sql += ")";

        Database.execute(sql);
    }

    /**
     * Updates an entity in a table with the same name of a model child but in
     * plural, e.g. a model <code>class Person extends Model</code> will have
     * all of its fields updated, in a table called <code>persons</code>.
     */
    public void update() {
        Method[] mt = this.getClass().getDeclaredMethods();
        List<Field> fields = ObjectUtility.getFields(mt, this);
        Object id = null;

        String sql = "UPDATE `" + ObjectUtility.tableOf(this) + "` SET ";
        for (int i = 0; i < fields.size(); i++) {
            Field field = fields.get(i);
            if (!field.getName().equals("id")) {
                sql += "`" + field.getName() + "` = '" + field.getValue() + "'";

                if ((i + 1) != fields.size()) {
                    sql += ", ";
                }
            } else {
                id = field.getValue();
            }
        }

        if (id == null) {
            System.out.println("id cannot be null");
            return;
        }

        sql += " WHERE `id` = " + id;

        Database.execute(sql);
    }

    public void delete() {
        Method[] mt = this.getClass().getDeclaredMethods();
        List<Field> fields = ObjectUtility.getFields(mt, this);
        Object id = null;
        String sql = "DELETE FROM `" + ObjectUtility.tableOf(this) + "`";

        for (Field f : fields) {
            if (f.getName().equals("id")) {
                id = f.getValue();
                break;
            }
        }

        if (id == null) {
            System.err.println("id cannot be null");
            return;
        }

        sql += " WHERE `id` = " + id;

        Database.execute(sql);
    }

    public static void create(Model model) {
        model.save();
    }

    public static void update(Model model) {
        model.update();
    }
    
    public static <M extends Model> M find(int id, Supplier<M> constructor) {
        M instance = constructor.get();
        Method[] methods = instance.getClass().getDeclaredMethods();
        List<Method> setters = new ArrayList<>();
        List<Field> fields = ObjectUtility.getFields(methods, instance);
        
        for (Method m : methods) {
            if (m.getName().contains("set")) {
                setters.add(m);
            }
        }
        
        String sql = "SELECT * FROM `" + ObjectUtility.tableOf(instance) + "` where `id` =" + id;
        
        try {
            ResultSet rs = Database.executeQuery(sql);

            while (rs.next()) {
                for (int i = 0; i < setters.size(); i++) {
                    Method tempMethod = setters.get(i);
                    for (int j = 0; j < fields.size(); j++) {
                        Field tempField = fields.get(j);
                        if (tempMethod.getName().toLowerCase().contains(tempField.getName())) {
                            tempMethod.invoke(instance, Database.getResult(rs, tempField.getType(), tempField.getName()));
                        }
                    }
                }
            }
        } catch (SQLException | IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
            System.err.println("Error: " + e);
        }
        return instance;
    }

    public static <M extends Model> List<M> all(Supplier<M> constructor) {
        M instance = constructor.get();
        List<M> models = new ArrayList<>();
        Method[] methods = instance.getClass().getDeclaredMethods();
        List<Method> setters = new ArrayList<>();
        List<Field> fields = ObjectUtility.getFields(methods, instance);

        for (Method m : methods) {
            if (m.getName().contains("set")) {
                setters.add(m);
            }
        }
        String sql = "SELECT * FROM `" + ObjectUtility.tableOf(instance) + "`";
        try {
            ResultSet rs = Database.executeQuery(sql);

            while (rs.next()) {
                M model = constructor.get();

                for (int i = 0; i < setters.size(); i++) {
                    Method tempMethod = setters.get(i);
                    for (int j = 0; j < fields.size(); j++) {
                        Field tempField = fields.get(j);
                        if (tempMethod.getName().toLowerCase().contains(tempField.getName())) {
                            tempMethod.invoke(model, Database.getResult(rs, tempField.getType(), tempField.getName()));
                        }
                    }
                }
                models.add(model);
            }
        } catch (SQLException | IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
            System.err.println("Error: " + e);
        }

        return models;
    }
    
}
