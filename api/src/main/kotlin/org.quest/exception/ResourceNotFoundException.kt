package org.quest.exception

import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.ResponseStatus
import java.lang.RuntimeException

@ResponseStatus(HttpStatus.NOT_FOUND)
class ResourceNotFoundException(
        private val resourceName: String, private val fieldName: String,
        private val fieldValue: Any
) : RuntimeException(String.format("%s not found with %s : '%s'", resourceName, fieldName, fieldValue)) {
    fun getResourceName(): String {
        return resourceName
    }

    fun getFieldName(): String {
        return fieldName
    }

    fun getFieldValue(): Any {
        return fieldValue
    }
}