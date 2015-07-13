package ryancheng.bus.retrofit;

import org.htmlcleaner.HtmlCleaner;
import org.htmlcleaner.TagNode;
import org.htmlcleaner.XPatherException;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Type;

import retrofit.converter.ConversionException;
import retrofit.converter.Converter;
import retrofit.mime.TypedInput;
import retrofit.mime.TypedOutput;

/**
 * Administrator
 * 2015/6/1 0001.
 */
public class HtmlConverter implements Converter {

    public HtmlConverter() {
    }

    @Override
    public Object fromBody(TypedInput body, Type type) throws ConversionException {
        InputStream io = null;
        try {
            io = body.in();
            HtmlCleaner hc = new HtmlCleaner();
            TagNode tn = hc.clean(io);
            return tn;
        } catch (IOException e) {
            e.printStackTrace();
            throw new ConversionException(e);
        } finally {
            if (io != null) {
                try {
                    io.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    @Override
    public TypedOutput toBody(Object object) {
        //todo
        return null;
    }
}
