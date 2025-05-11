package edu.school21.constants.converters;

import edu.school21.constants.Gender;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

import java.util.stream.Stream;

@Converter(autoApply = true)
public class GenderConverter implements AttributeConverter<Gender, String> {
    @Override
    public String convertToDatabaseColumn(Gender gender) {
        if (gender == null) {
            return null;
        }
        return gender.getValue();
    }

    @Override
    public Gender convertToEntityAttribute(String value) {
        if (value == null) {
            return null;
        }
        return Stream.of(Gender.values())
                .filter(c -> c.getValue().equals(value))
                .findFirst()
                .orElseThrow(IllegalArgumentException::new);
    }
}