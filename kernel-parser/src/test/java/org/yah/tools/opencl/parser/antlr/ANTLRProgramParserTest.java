package org.yah.tools.opencl.parser.antlr;

import org.junit.Ignore;
import org.junit.Test;
import org.yah.tools.opencl.parser.model.ParsedProgram;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

public class ANTLRProgramParserTest {

    @Ignore
    @Test
    public void testParse() throws IOException {
        ANTLRProgramParser ANTLRProgramParser = new ANTLRProgramParser();
        try (InputStream is = ANTLRProgramParserTest.class.getResourceAsStream("/vector_sum.cl")){
            if (is == null) throw new FileNotFoundException();
            ParsedProgram parsedProgram = ANTLRProgramParser.parse(is);
            System.out.println(parsedProgram.getKernels());
        }
    }
}