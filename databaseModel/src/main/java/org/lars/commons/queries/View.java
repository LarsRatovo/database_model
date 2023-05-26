package org.lars.commons.queries;

import org.lars.commons.queries.creator.Creator;
import org.lars.commons.queries.creator.CreatorException;
import org.lars.commons.queries.creator.annotations.Linked;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.List;
import java.util.Properties;

public class View<M> extends Select<M>{
}
