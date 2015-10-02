package org.optaplanner.examples.nurserostering.domain;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import java.util.List;
import org.optaplanner.examples.common.domain.AbstractPersistable;

@XStreamAlias("Coordinator")
public class Coordinator extends AbstractPersistable {
    private String code;
    private String name;
    private String email;
    private List<CourseType> courseTypes;

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public List<CourseType> getCourseTypes() {
        return courseTypes;
    }

    public void setCourseTypes(List<CourseType> courseTypes) {
        this.courseTypes = courseTypes;
    }
}
