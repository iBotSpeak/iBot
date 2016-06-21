package pl.themolka.ibot.command;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface BotCommand {
    String name();

    String description() default "No description provided.";

    String[] flags() default "";

    String usage() default "";
}
