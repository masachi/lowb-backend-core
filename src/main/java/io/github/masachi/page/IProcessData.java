package io.github.masachi.page;

import java.util.List;

public interface IProcessData<T, E> {
    List<E> processData(List<T> rows);
}
