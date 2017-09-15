package com.typany.utilities;

import android.util.Log;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Set;

/**
 * Created by feixuezheng on 2017/9/15.
 */

public class INIFileWraper {
    private final static String TAG = "INIFileWraper";
    private final static int BUF_SIZE = 8192;
    private HashMap mINISections = new HashMap();

    public INIFileWraper(String filePath, String encoding){
        try {
            loadFile(new FileInputStream(filePath), encoding);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    public INIFileWraper(InputStream inputStream, String encoding) {
        loadFile(inputStream, encoding);
    }

    private void  loadFile(InputStream inputStream, String encoding) {
        int iPos = -1;
        String strLine = null;
        String strSection = null;
        String strRemarks = null;
        BufferedReader objBRdr = null;
        InputStreamReader objFRdr = null;
        INISection objSec = null;

        try {
            objFRdr = new InputStreamReader(inputStream, encoding);
            if (objFRdr != null) {
                // default is 8k for android
                objBRdr = new BufferedReader(objFRdr, BUF_SIZE);
                if (objBRdr != null) {

                    while (objBRdr.ready()) {
                        iPos = -1;
                        strLine = objBRdr.readLine();
//                        i++;
                        if (strLine == null) {
                            continue;
                        } else {
                            strLine = strLine.trim();
                        }

                        if (/*(strLine == null) || */(strLine.length() == 0)) {
                        }
                        //else if (strLine.substring(0, 1).equals(";")) {

                        else if (strLine.charAt(0) == ';') {
                            Log.d(TAG, "Got 1 line comment!!!");
                        }

                        else if (strLine.startsWith("[") && strLine.endsWith("]")) {
                            // Section start reached create new section
                            if (objSec != null) {
                                mINISections.put(strSection, objSec);
                            }
                            objSec = null;
                            strSection = strLine.substring(1, strLine.length() - 1).trim();
                            objSec = new INISection(strSection, strRemarks);
                            strRemarks = null;
                        }
                        else if ( (iPos = strLine.indexOf("=")) > 0 && objSec != null) {
                            objSec.setProperty(strLine.substring(0, iPos).trim(),
                                    strLine.substring(iPos + 1).trim(),
                                    strRemarks);
                            strRemarks = null;
                        }
                    }
                    if (objSec != null) {
                        mINISections.put(strSection, objSec);
                    }
                }
            }
        }
        catch (Exception e) {
            mINISections.clear();
            e.printStackTrace();
        }
        finally {
            if (objBRdr != null) {
                try {
                    objBRdr.close();
                    objBRdr = null;
                }
                catch(Exception e){
                    e.printStackTrace();
                }
            }
            if (objFRdr != null) {
                try {
                    objFRdr.close();
                    objFRdr = null;
                }
                catch(Exception e){
                    e.printStackTrace();
                }
            }
            if (objSec != null) {
                objSec = null;
            }
        }
    }

    public String getAllInfo() {
        Iterator iter = null;
        StringBuilder builder = new StringBuilder();

        try {
            if (mINISections != null && this.mINISections.size() > 0) {
                for (iter = mINISections.entrySet().iterator(); ; iter.hasNext()) {
                    Map.Entry<String, Object> entry = (Map.Entry<String, Object>) iter.next();
                    builder.append("[");
                    builder.append(entry.getKey() + ":");

                    builder.append(">>");

                    INISection ini = (INISection) entry.getValue();
                    if (ini != null) {
                        builder.append(ini.mhmapProps.toString());
                    }

                    builder.append("]");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return builder.toString();
    }

    public String[] getAllSectionNames() {
        int iCntr = 0;
        Iterator iter = null;
        String[] arrRet = null;

        try {
            if (this.mINISections != null && this.mINISections.size() > 0) {
                arrRet = new String[this.mINISections.size()];
                for (iter = this.mINISections.keySet().iterator(); ; iter.hasNext()) {
                    arrRet[iCntr] = (String) iter.next();
                    iCntr++;
                }
            }
        }
        catch (NoSuchElementException NSEExIgnore) {
        }
        finally {
            if (iter != null) {
                iter = null;
            }
        }
        return arrRet;
    }

    public String getStringProperty(String pstrSection, String pstrProp) {
        String strRet = null;
        INIProperty objProp = null;
        INISection objSec = null;

        if (mINISections == null) return null;
        objSec = (INISection) this.mINISections.get(pstrSection);
        if (objSec != null) {
            objProp = objSec.getProperty(pstrProp);
            if (objProp != null) {
                strRet = objProp.getPropValue();
                objProp = null;
            }
            objSec = null;
        }
        return strRet;
    }



    /*------------------------------------------------------------------------------
     * Private class representing the INI Property.
     *----------------------------------------------------------------------------*/
    /**
     * This class represents a key value pair called property in an INI file.
     * @author Prasad P. Khandekar
     * @version 1.0
     * @since 1.0
     */
    public static class INIProperty {
        /** Variable to hold name of this property */
        private String mstrName;
        /** Variable to hold value of this property */
        private String mstrValue;
        /** Variable to hold comments associated with this property */
        private String mstrComments;

        /**
         * Constructor
         * @param pstrName the name of this property.
         * @param pstrValue the value of this property.
         */
        public INIProperty(String pstrName, String pstrValue) {
            this.mstrName = pstrName;
            this.mstrValue = pstrValue;
        }

        /**
         * Constructor
         * @param pstrName the name of this property.
         * @param pstrValue the value of this property.
         * @param pstrComments the comments associated with this property.
         */
        public INIProperty(String pstrName, String pstrValue, String pstrComments) {
            this.mstrName = pstrName;
            this.mstrValue = pstrValue;
            //this.mstrComments = delRemChars(pstrComments);
        }

        /**
         * Returns the string identifier (key part) of this property.
         * @return the string identifier of this property.
         */
        public String getPropName() {
            return this.mstrName;
        }

        /**
         * Returns value of this property. If value contains a reference to
         * environment avriable then this reference is replaced by actual value
         * before the value is returned.
         * @return the value of this property.
         */
        public String getPropValue() {
            String strRet = this.mstrValue;

            /** Support \f.
             int intStart = 0;
             int intEnd = 0;
             String strVal = "";
             intStart = strRet.indexOf("\\");
             if (intStart >= 0) {
             switch (strRet.charAt(intStart + 1)) {
             case 'f':
             strVal = "\n";
             break;
             default:
             break;
             }
             strRet = strRet.substring(0, intStart) + strVal +
             strRet.substring(intStart + 2);
             }
             */

            /** .
             intStart = strRet.indexOf("%");
             if (intStart >= 0) {
             intEnd = strRet.indexOf("%", intStart + 1);
             if (strVal != null) {
             strRet = strRet.substring(0, intStart) + strVal +
             strRet.substring(intEnd + 1);
             }
             }*/
            return strRet;
        }

        /**
         * Returns comments associated with this property.
         * @return the associated comments if any.
         */
        public String getPropComments() {
            return this.mstrComments;
        }

        /**
         * Sets the string identifier (key part) of a property
         * @param pstrName the string identifier of a property
         */
        public void setPropName(String pstrName) {
            this.mstrName = pstrName;
        }

        /**
         * Sets the property value
         * @param pstrValue the value for the property
         */
        public void setPropValue(String pstrValue) {
            this.mstrValue = pstrValue;
        }

        /**
         * Sets the comments for a property
         * @param pstrComments the comments
         */
        public void setPropComments(String pstrComments) {
            //this.mstrComments = delRemChars(pstrComments);
        }

        /* (non-Javadoc)
         * @see java.lang.Object#toString()
         */
        public String toString() {
            String strRet = "";

            if (this.mstrComments != null) {
                strRet = addRemChars(mstrComments);
            }
            strRet = strRet + this.mstrName + " = " + this.mstrValue;
            return strRet;
        }
    }

    /**
     * Class to represent the individual ini file section.
     * @author Prasad P. Khandekar
     * @version 1.0
     * @since 1.0
     */
    public static class INISection {
        /** Variable to hold any comments associated with this section */
        private String mstrComment;

        /** Variable to hold the section name. */
        private String mstrName;

        /** Variable to hold the properties falling under this section. */
        private HashMap mhmapProps;

        /**
         * Construct a new section object identified by the name specified in
         * parameter.
         * @param pstrSection The new sections name.
         */
        public INISection(String pstrSection) {
            this.mstrName = pstrSection;
            this.mhmapProps = new HashMap();
        }

        /**
         * Construct a new section object identified by the name specified in
         * parameter and associated comments.
         * @param pstrSection The new sections name.
         * @param pstrComments the comments associated with this section.
         */
        public INISection(String pstrSection, String pstrComments) {
            this.mstrName = pstrSection;
            //this.mstrComment = delRemChars(pstrComments);
            this.mhmapProps = new HashMap();
        }

        /**
         * Returns any comments associated with this section
         * @return the comments
         */
        public String getSecComments() {
            return this.mstrComment;
        }

        /**
         * Returns name of the section.
         * @return Name of the section.
         */
        public String getSecName() {
            return this.mstrName;
        }

        /**
         * Sets the comments associated with this section.
         * @param pstrComments the comments
         */
        public void setSecComments(String pstrComments) {
            //this.mstrComment = delRemChars(pstrComments);
        }

        /**
         * Sets the section name.
         * @param pstrName the section name.
         */
        public void setSecName(String pstrName) {
            this.mstrName = pstrName;
        }

        /**
         * Removes specified property value from this section.
         * @param pstrProp The name of the property to be removed.
         */
        public void removeProperty(String pstrProp) {
            if (this.mhmapProps.containsKey(pstrProp)) {
                this.mhmapProps.remove(pstrProp);
            }
        }

        /**
         * Creates or modifies the specified property value.
         * @param pstrProp The name of the property to be created or modified.
         * @param pstrValue The new value for the property.
         * @param pstrComments the associated comments
         */
        public void setProperty(String pstrProp, String pstrValue,
                                String pstrComments) {
            this.mhmapProps.put(pstrProp, new INIProperty(pstrProp, pstrValue,
                    pstrComments));
        }

        /**
         * Returns a map of all properties.
         * @return a map of all properties
         */
        public Map getProperties() {
            return Collections.unmodifiableMap(this.mhmapProps);
        }

        /**
         * Returns a string array containing names of all the properties under
         * this section.
         * @return the string array of property names.
         */
        public String[] getPropNames() {
            int iCntr = 0;
            String[] arrRet = null;
            Iterator iter = null;

            try {
                if (this.mhmapProps.size() > 0) {
                    arrRet = new String[this.mhmapProps.size()];
                    for (iter = this.mhmapProps.keySet().iterator(); iter.hasNext(); ) {
                        arrRet[iCntr] = (String) iter.next();
                        iCntr++;
                    }
                }
            }
            catch (NoSuchElementException NSEExIgnore) {
                arrRet = null;
            }
            return arrRet;
        }

        public boolean containProperty(String pstrProp) {
            return this.mhmapProps.containsKey(pstrProp);
        }

        /**
         * Returns underlying value of the specified property.
         * @param pstrProp the property whose underlying value is to be etrieved.
         * @return the property value.
         */
        public INIProperty getProperty(String pstrProp) {
            INIProperty objRet = (INIProperty)this.mhmapProps.get(pstrProp);
            return objRet;
        }

        /* (non-Javadoc)
         * @see java.lang.Object#toString()
         */
        public String toString() {
            Set colKeys = null;
            String strRet = "";
            Iterator iter = null;
            INIProperty objProp = null;
            StringBuffer objBuf = new StringBuffer();

            if (this.mstrComment != null) {
                objBuf.append(addRemChars(this.mstrComment));
            }
            objBuf.append("[" + this.mstrName + "]\r\n");
            colKeys = this.mhmapProps.keySet();
            if (colKeys != null) {
                iter = colKeys.iterator();
                if (iter != null) {
                    while (iter.hasNext()) {
                        objProp = (INIProperty)this.mhmapProps.get(iter.next());
                        objBuf.append(objProp.toString());
                        objBuf.append("\r\n");
                        objProp = null;
                    }
                }
            }
            strRet = objBuf.toString();

            objBuf = null;
            iter = null;
            colKeys = null;
            return strRet;
        }
    }

    private static String addRemChars(String pstrSrc) {
        int intLen = 2;
        int intPos = 0;
        int intPrev = 0;

        String strLeft = null;
        String strRight = null;

        if (pstrSrc == null) {
            return null;
        }
        while (intPos >= 0) {
            intLen = 2;
            intPos = pstrSrc.indexOf("\r\n", intPrev);
            if (intPos < 0) {
                intLen = 1;
                intPos = pstrSrc.indexOf("\n", intPrev);
                if (intPos < 0) {
                    intPos = pstrSrc.indexOf("\r", intPrev);
                }
            }
            if (intPos == 0) {
                pstrSrc = ";\r\n" + pstrSrc.substring(intPos + intLen);
                intPrev = intPos + intLen + 1;
            } else if (intPos > 0) {
                strLeft = pstrSrc.substring(0, intPos);
                strRight = pstrSrc.substring(intPos + intLen);
                if (strRight == null) {
                    pstrSrc = strLeft;
                } else if (strRight.length() == 0) {
                    pstrSrc = strLeft;
                } else {
                    pstrSrc = strLeft + "\r\n;" + strRight;
                }
                intPrev = intPos + intLen + 1;
            }
        }
        if (!pstrSrc.substring(0, 1).equals(";")) {
            pstrSrc = ";" + pstrSrc;
        }
        pstrSrc = pstrSrc + "\r\n";
        return pstrSrc;
    }

    public void clear(){
        mINISections.clear();
        mINISections = null;
    }
}
