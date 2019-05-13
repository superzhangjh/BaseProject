package annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Created by zhang on 2019/4/10 15:39
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.SOURCE)
public @interface API {
    /**
     * this name of api
     */
    String name() default "";

    /**
     * the compiler filepath
     */
    String packageName() default "";

    /**
     * the host of api
     */
    String host() default "";
}
