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
package org.jloquent;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author derickfelix
 * @date Feb 24, 2018
 */
public abstract class Model {

    private Integer id;

    /**
     * Creates a new entity into a table with the same name of a model child but
     * in plural, e.g. a model <code>class Person extends Model</code> will have
     * all of its fields persisted into a table called <code>persons</code>.
     */
    public void save() {
        Method[] mt = this.getClass().getDeclaredMethods();
        List<Field> fields = Utility.getFields(mt, this, false);
        Connector connector = Connector.getInstance();

        String sql = "INSERT INTO " + Utility.tableOf(this) + " (";
        for (int i = 0; i < fields.size(); i++) {
            Field field = fields.get(i);

            sql += field.getName();

            if ((i + 1) < fields.size()) {
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

        connector.execute(sql);
        System.out.println(sql);
    }

    /**
     * Updates an entity in a table with the same name of a model child but in
     * plural, e.g. a model <code>class Person extends Model</code> will have
     * all of its fields updated, in a table called <code>persons</code>.
     */
    public void update() {
        Method[] mt = this.getClass().getDeclaredMethods();
        List<Field> fields = Utility.getFields(mt, this, false);
        Connector connector = Connector.getInstance();

        String sql = "UPDATE " + Utility.tableOf(this) + " SET ";
        for (int i = 0; i < fields.size(); i++) {
            Field field = fields.get(i);
            
            sql += field.getName() + " = '" + field.getValue() + "'";

            if ((i + 1) < fields.size()) {
                sql += ", ";
            }
        }

        if (id == null) {
            Logger.getLogger(getClass().getName()).log(Level.SEVERE, "id cannot be null");
            return;
        }

        sql += " WHERE id = " + id;

        connector.execute(sql);
        System.out.println(sql);
    }

    public void delete() {
        String sql = "DELETE FROM " + Utility.tableOf(this);
        Connector connector = Connector.getInstance();

        if (id == null) {
            Logger.getLogger(getClass().getName()).log(Level.SEVERE, "id cannot be null");
            return;
        }

        sql += " WHERE id = " + id;

        connector.execute(sql);
        System.out.println(sql);
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
        List<Field> fields = Utility.getFields(methods, instance, true);
        Connector connector = Connector.getInstance();

        for (Method m : methods) {
            if (m.getName().contains("set")) {
                setters.add(m);
            }
        }

        String sql = "SELECT * FROM " + Utility.tableOf(instance) + " WHERE id = " + id;

        try {
            ResultSet rs = connector.executeQuery(sql);

            while (rs.next()) {
                instance.setId(id);
                for (int i = 0; i < setters.size(); i++) {
                    Method tempMethod = setters.get(i);
                    for (int j = 0; j < fields.size(); j++) {
                        Field tempField = fields.get(j);
                        if (tempMethod.getName().toLowerCase().contains(tempField.getName())) {
                            tempMethod.invoke(instance, connector.getResult(rs, tempField.getType(), tempField.getName()));
                        }
                    }
                }
            }

        } catch (SQLException | IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
            Logger.getLogger(Model.class.getName()).log(Level.SEVERE, "Failed execute query", e);
        }
        System.out.println(sql);

        return instance;
    }

    public static <M extends Model> List<M> all(Supplier<M> constructor) {
        M instance = constructor.get();
        List<M> models = new ArrayList<>();
        Method[] methods = instance.getClass().getDeclaredMethods();
        List<Method> setters = new ArrayList<>();
        List<Field> fields = Utility.getFields(methods, instance, true);
        Connector connector = Connector.getInstance();

        for (Method m : methods) {
            if (m.getName().contains("set")) {
                setters.add(m);
            }
        }

        String sql = "SELECT * FROM " + Utility.tableOf(instance);
        try {
            ResultSet rs = connector.executeQuery(sql);

            while (rs.next()) {
                M model = constructor.get();
                model.setId(rs.getInt("id"));

                for (int i = 0; i < setters.size(); i++) {
                    Method tempMethod = setters.get(i);
                    for (int j = 0; j < fields.size(); j++) {
                        Field tempField = fields.get(j);
                        if (tempMethod.getName().toLowerCase().contains(tempField.getName())) {
                            tempMethod.invoke(model, connector.getResult(rs, tempField.getType(), tempField.getName()));
                        }
                    }
                }
                models.add(model);
            }
        } catch (SQLException | IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
            Logger.getLogger(Model.class.getName()).log(Level.SEVERE, "Failed to execute query", e);
        }
        System.out.println(sql);

        return models;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

}
