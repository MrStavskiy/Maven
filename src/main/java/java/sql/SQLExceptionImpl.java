

import java.sql.SQLException;
import java.util.function.Consumer;

class SQLExceptionImpl extends SQLException {
    public SQLExceptionImpl(String reason, String SQLState, int vendorCode) {
        super(reason, SQLState, vendorCode);
    }

    public void forEach() {
        forEach(null);
    }

    @Override
    public void forEach(Consumer<? super Throwable> action) {
        super.forEach(action);
    }
}
