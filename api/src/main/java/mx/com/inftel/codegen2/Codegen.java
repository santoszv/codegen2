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

package mx.com.inftel.codegen2;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Marks an JPA class for processing with Codegen2 Annotation Processing Tool.
 */
@SuppressWarnings("unused")
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface Codegen {

    /**
     * Classname for CRUD.
     * <p>The new class is generated in the same package as JPA class.</p>
     *
     * @return Classname
     */
    String crud() default "";

    /**
     * Classname for DTO.
     * <p>The new class is generated in the same package as JPA class.</p>
     *
     * @return Classname
     */
    String dto() default "";

    /**
     * Classname for DTI.
     * <p>The new class is generated in the same package as JPA class.</p>
     *
     * @return Classname
     */
    String dti() default "";

    /**
     * Suffix name for methods.
     * <p>The methods will use this name as the common suffix, in example,
     * with "User" the CRUD will have "findUser", "countUser", "listUser",
     * etc.</p>
     *
     * @return Suffix name
     */
    String methods() default "";
}
