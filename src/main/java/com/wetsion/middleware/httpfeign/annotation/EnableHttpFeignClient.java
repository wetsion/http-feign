package com.wetsion.middleware.httpfeign.annotation;

import com.wetsion.middleware.httpfeign.HttpFeignClientRegistrar;
import org.springframework.context.annotation.Import;
import java.lang.annotation.*;

/**
 * 开启rms 声明式调用rest接口
 *
 * @author weixin
 * @version 1.0
 * @CLassName EnableHttpFeignClient
 * @date 2019/3/11 2:45 PM
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Documented
@Import(HttpFeignClientRegistrar.class)
public @interface EnableHttpFeignClient {

    String[] value() default {};

    /**
     * Base packages to scan for annotated components.
     * <p>
     * {@link #value()} is an alias for (and mutually exclusive with) this attribute.
     * <p>
     * Use {@link #basePackageClasses()} for a type-safe alternative to String-based
     * package names.
     *
     * @return the array of 'basePackages'.
     */
    String[] basePackages() default {};

    /**
     * Type-safe alternative to {@link #basePackages()} for specifying the packages to
     * scan for annotated components. The package of each class specified will be scanned.
     * <p>
     * Consider creating a special no-op marker class or interface in each package that
     * serves no purpose other than being referenced by this attribute.
     *
     * @return the array of 'basePackageClasses'.
     */
    Class<?>[] basePackageClasses() default {};

    /**
     * List of classes annotated with @FeignClient. If not empty, disables classpath scanning.
     * @return
     */
    Class<?>[] clients() default {};
}
