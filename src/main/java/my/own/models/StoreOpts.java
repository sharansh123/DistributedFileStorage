package my.own.models;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.function.Function;

@Data
@AllArgsConstructor
public class StoreOpts {
    Function<String,String> transform;

}
