package ca.ulaval.glo4003.dataaccessobjects;

import ca.ulaval.glo4003.exceptions.UniqueConstraintException;
import ca.ulaval.glo4003.models.Record;

import javax.persistence.Column;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

public class UniqueConstraintValidator {

    private List<Field> uniqueFields;

    public void validate(List<Record> list, Record element) throws UniqueConstraintException {
        for (Field field : getUniqueFields(element.getClass())) {
            for (Record elem : list) {
                try {
                    if (field.get(element).equals(field.get(elem)) && element.getId() != elem.getId()) {
                        throw new UniqueConstraintException();
                    }
                } catch (IllegalAccessException e) {
                    // The field should exists.
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
