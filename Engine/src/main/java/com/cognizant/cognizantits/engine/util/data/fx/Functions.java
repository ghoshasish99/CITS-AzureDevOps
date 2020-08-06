/*
 * Copyright 2014 - 2017 Cognizant Technology Solutions
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.cognizant.cognizantits.engine.util.data.fx;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * 
 */
public class Functions extends FunctionsLib {

    private static final Logger LOG = Logger.getLogger(Functions.class.getName());

    public Object Date(String... args) {
        try {
            int paramLength = args.length, dx;
            switch (paramLength) {
                case 1:
                    dx = Integer.parseInt((args[0]).split("\\.")[0]);
                    return this.getDate(dx);
                case 2:
                    dx = Integer.parseInt((args[0]).split("\\.")[0]);
                    String format = args[1];
                    return this.getDate(dx, format);
            }
        } catch (Exception ex) {
            LOG.log(Level.SEVERE, ex.getMessage(), ex);
        }
        return this.getDate(0);
    }

    public Object Round(String... args) {
        try {
            Double val = Double.parseDouble((args[0]));
            return this.getRound(val);
        } catch (Exception ex) {
            LOG.log(Level.SEVERE, ex.getMessage(), ex);
        }
        return args[0];
    }

    public Object Random(String... args) {
        try {
            int paramLength = args.length;
            switch (paramLength) {
                case 1:
                    Double len = Double.parseDouble(args[0]);
                    return this.getRandom(len);
                case 2:
                    Double from = Double.parseDouble(args[0]);
                    Double to = Double.parseDouble(args[1]);
                    return this.getRandom(from, to);
            }
        } catch (Exception ex) {
            LOG.log(Level.SEVERE, ex.getMessage(), ex);
        }
        return 0;
    }

    public Object Pow(String... args) {
        try {
            Double a = Double.parseDouble(args[0]);
            Double b = Double.parseDouble(args[1]);
            return this.getPow(a, b);
        } catch (Exception ex) {
            LOG.log(Level.SEVERE, ex.getMessage(), ex);
        }
        return 0;
    }

    public Object Min(String... args) {
        try {
            Double a = Double.parseDouble(args[0]);
            Double b = Double.parseDouble(args[1]);
            return this.getMin(a, b);
        } catch (Exception ex) {
            LOG.log(Level.SEVERE, ex.getMessage(), ex);
        }
        return 0;
    }

    public Object Max(String... args) {
        try {
            Double a = Double.parseDouble(args[0]);
            Double b = Double.parseDouble(args[1]);
            return this.getMax(a, b);
        } catch (Exception ex) {
            LOG.log(Level.SEVERE, ex.getMessage(), ex);
        }
        return 0;
    }

    public Object Concat(String... args) {
        String op = "";
        try {
            if (args.length > 0) {
                for (String arg : args) {
                    op += arg;
                }
            }
        } catch (Exception ex) {
            LOG.log(Level.SEVERE, ex.getMessage(), ex);
        }
        return op;
    }

}
