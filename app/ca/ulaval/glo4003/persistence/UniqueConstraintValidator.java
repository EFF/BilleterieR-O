package ca.ulaval.glo4003.persistence;

import ca.ulaval.glo4003.domain.Record;
import org.apache.commons.beanutils.PropertyUtils;

import javax.persistence.Column;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

public class UniqueConstraintValidator<T extends Record> {

    private List<Field> uniqueFields;

    public void validate(List<T> list, T element) {
        for (Field field : getUniqueFields(element.getClass())) {
            for (T element2 : list) {
                try {
                    Object value1 = PropertyUtils.getProperty(element, field.getName());
                    Object value2 = PropertyUtils.getProperty(element2, field.getName());
                    if (value1.equals(value2) && element.getId() != element2.getId()) {
                        throw new UniqueValidationException();
                    }
                } catch (ReflectiveOperationException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private List<Field> getUniqueFields(Class clazz) {
        if (uniqueFields == null) {
            uniqueFields = getUniqueAnnotatedField(clazz);
        }
        return uniqueFields;
    }

    private List<Field> getUniqueAnnotatedField(Class clazz) {
        List<Field> fields = new ArrayList<>();
        for (Field field : clazz.getDeclaredFields()) {
            if (field.isAnnotationPresent(Column.class)) {
                Column columnAnnotation = field.getAnnotation(Column.class);
                if (columnAnnotation.unique()) {
                    fields.add(field);
                }
            }
        }
        return fields;
    }
}
