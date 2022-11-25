/*
 * Copyright 2022 Santos Zatarain Vera
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package mx.com.inftel.codegen.apt

import mx.com.inftel.codegen.apt.model.ClassModel
import mx.com.inftel.codegen.apt.model.PropertyModel
import mx.com.inftel.codegen.apt.model.TypeModel
import java.io.BufferedWriter
import javax.annotation.processing.ProcessingEnvironment
import javax.lang.model.element.AnnotationMirror
import javax.lang.model.element.TypeElement

fun writeDTO(processingEnvironment: ProcessingEnvironment, bufferedWriter: BufferedWriter, classModel: ClassModel) {
    bufferedWriter.appendLine("// Origin: ${classModel.qualifiedName}")
    if (classModel.packageName.isNotBlank()) {
        bufferedWriter.appendLine()
        bufferedWriter.appendLine("package ${classModel.packageName};")
    }
    bufferedWriter.appendLine()
    bufferedWriter.appendLine("public class ${classModel.dtoName} implements java.io.Serializable {")
    for (propertyModel in classModel.properties) {
        when {
            propertyModel.isColumn -> writeColumnProperty(bufferedWriter, propertyModel)
            propertyModel.isJoinColumn -> writeJoinColumnProperty(processingEnvironment, bufferedWriter, propertyModel)
        }
    }
    bufferedWriter.appendLine("}")
}

private fun writeColumnProperty(bufferedWriter: BufferedWriter, propertyModel: PropertyModel) {
    val propertyName = propertyModel.propertyName
    val getterName = propertyModel.getterName
    val setterName = propertyModel.setterName
    val propertyType = propertyModel.propertyType.toCode()
    bufferedWriter.appendLine("""""")
    bufferedWriter.appendLine("""    private $propertyType $propertyName;""")
    bufferedWriter.appendLine("""""")
    writeValidations(bufferedWriter, propertyModel.validations)
    bufferedWriter.appendLine("""    public $propertyType $getterName() {""")
    bufferedWriter.appendLine("""        return this.$propertyName;""")
    bufferedWriter.appendLine("""    }""")
    bufferedWriter.appendLine("""""")
    bufferedWriter.appendLine("""    public void $setterName($propertyType $propertyName) {""")
    bufferedWriter.appendLine("""        this.$propertyName = $propertyName;""")
    bufferedWriter.appendLine("""    }""")
}

private fun writeJoinColumnProperty(processingEnvironment: ProcessingEnvironment, bufferedWriter: BufferedWriter, propertyModel: PropertyModel) {
    val propertyName = propertyModel.propertyName
    val getterName = propertyModel.getterName
    val setterName = propertyModel.setterName
    val propertyType = propertyModel.propertyType as? TypeModel.ReferenceType.Class ?: return
    val joinClassModel = ClassModel(processingEnvironment, propertyType.declaredType.asElement() as TypeElement)
    val idPropertyModel = joinClassModel.idProperty ?: return
    val idPropertyType = if (propertyModel.isNullable) {
        idPropertyModel.propertyType.toNullable().toCode()
    } else {
        idPropertyModel.propertyType.toNonNullable().toCode()
    }
    bufferedWriter.appendLine("""""")
    bufferedWriter.appendLine("""    private $idPropertyType ${propertyName}Id;""")
    bufferedWriter.appendLine("""""")
    writeValidations(bufferedWriter, propertyModel.validations)
    bufferedWriter.appendLine("""    public $idPropertyType ${getterName}Id() {""")
    bufferedWriter.appendLine("""        return this.${propertyName}Id;""")
    bufferedWriter.appendLine("""    }""")
    bufferedWriter.appendLine("""""")
    bufferedWriter.appendLine("""    public void ${setterName}Id($idPropertyType ${propertyName}Id) {""")
    bufferedWriter.appendLine("""        this.${propertyName}Id = ${propertyName}Id;""")
    bufferedWriter.appendLine("""    }""")
}

private fun writeValidations(bufferedWriter: BufferedWriter, validations: List<AnnotationMirror>) {
    for (validation in validations) {
        val annotationElement = validation.annotationType.asElement() as TypeElement
        bufferedWriter.write("    @${annotationElement.qualifiedName}")
        if (validation.elementValues.isNotEmpty()) {
            bufferedWriter.write("(")
            var first = true
            for ((key, value) in validation.elementValues) {
                if (first) {
                    first = false
                } else {
                    bufferedWriter.write(", ")
                }
                bufferedWriter.write("${key.simpleName} = $value")
            }
            bufferedWriter.write(")")
        }
        bufferedWriter.appendLine()
    }
}