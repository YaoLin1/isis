package org.apache.isis.core.metamodel.facets;

import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.Method;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.is;

public class Annotations_getAnnotations_on_Parameter_Test {


    @Inherited
    @Target({ ElementType.PARAMETER, ElementType.ANNOTATION_TYPE })
    @Retention(RetentionPolicy.RUNTIME)
    public @interface DomainObj { // cf @DomainObject
        enum Publishng { // cf Publishing enum
            YES,
            NO,
            NOT_SPECIFIED
        }
        Publishng publishng() default Publishng.NOT_SPECIFIED;
    }

    @DomainObj(publishng = DomainObj.Publishng.YES)
    @Inherited
    @Target({ ElementType.PARAMETER, ElementType.ANNOTATION_TYPE })
    @Retention(RetentionPolicy.RUNTIME)
    @interface Published {
    }

    @DomainObj(publishng = DomainObj.Publishng.NO)
    @Inherited
    @Target({ ElementType.PARAMETER, ElementType.ANNOTATION_TYPE })
    @Retention(RetentionPolicy.RUNTIME)
    @interface NotPublished {
    }

    @Published
    @Inherited
    @Target({ ElementType.PARAMETER, ElementType.ANNOTATION_TYPE })
    @Retention(RetentionPolicy.RUNTIME)
    @interface MetaPublished {
    }

    @Test
    public void direct() throws Exception {

        class SomeDomainObject {
            public void updateName(
                    @DomainObj(publishng = DomainObj.Publishng.YES)
                    String name
            ) {}
        }

        Method method = SomeDomainObject.class.getMethod("updateName", String.class);
        final List<DomainObj> annotations = Annotations.getAnnotations(method, 0, DomainObj.class);

        Assert.assertThat(annotations.size(), is(1));

        Assert.assertThat(annotations.get(0).publishng(), is(DomainObj.Publishng.YES));
    }

    @Test
    public void meta() throws Exception {

        class SomeDomainObject {
            public void updateName(
                    @Published
                    String name
            ) {}
        }

        Method method = SomeDomainObject.class.getMethod("updateName", String.class);
        final List<DomainObj> annotations = Annotations.getAnnotations(method, 0, DomainObj.class);

        Assert.assertThat(annotations.size(), is(1));

        Assert.assertThat(annotations.get(0).publishng(), is(DomainObj.Publishng.YES));
    }

    @Test
    public void metaMeta() throws Exception {

        class SomeDomainObject {
            public void updateName(
                    @MetaPublished
                    String name
            ) {}
        }

        Method method = SomeDomainObject.class.getMethod("updateName", String.class);
        final List<DomainObj> annotations = Annotations.getAnnotations(method, 0, DomainObj.class);

        Assert.assertThat(annotations.size(), is(1));

        Assert.assertThat(annotations.get(0).publishng(), is(DomainObj.Publishng.YES));
    }

    @Test
    public void meta_and_metaMeta() throws Exception {

        class SomeDomainObject {
            public void updateName(
                    @MetaPublished
                    @Published
                    String name
            ) {}
        }

        Method method = SomeDomainObject.class.getMethod("updateName", String.class);
        final List<DomainObj> annotations = Annotations.getAnnotations(method, 0, DomainObj.class);

        Assert.assertThat(annotations.size(), is(2));

        Assert.assertThat(annotations.get(0).publishng(), is(DomainObj.Publishng.YES));
        Assert.assertThat(annotations.get(1).publishng(), is(DomainObj.Publishng.YES));
    }

    @Test
    public void meta_overrides_metaMeta() throws Exception {

        class SomeDomainObject {
            public void updateName(
                    @MetaPublished
                    @NotPublished
                    String name
            ) {}
        }

        Method method = SomeDomainObject.class.getMethod("updateName", String.class);
        final List<DomainObj> annotations = Annotations.getAnnotations(method, 0, DomainObj.class);

        Assert.assertThat(annotations.size(), is(2));

        Assert.assertThat(annotations.get(0).publishng(), is(DomainObj.Publishng.NO));
        Assert.assertThat(annotations.get(1).publishng(), is(DomainObj.Publishng.YES));
    }

    @Test
    public void direct_overrides_metaMeta() throws Exception {

        class SomeDomainObject {
            public void updateName(
                    @MetaPublished
                    @Published
                    @DomainObj(publishng = DomainObj.Publishng.NO)
                    String name
            ) {}
        }

        Method method = SomeDomainObject.class.getMethod("updateName", String.class);
        final List<DomainObj> annotations = Annotations.getAnnotations(method, 0, DomainObj.class);

        Assert.assertThat(annotations.size(), is(3));

        Assert.assertThat(annotations.get(0).publishng(), is(DomainObj.Publishng.NO));
        Assert.assertThat(annotations.get(1).publishng(), is(DomainObj.Publishng.YES));
        Assert.assertThat(annotations.get(2).publishng(), is(DomainObj.Publishng.YES));
    }


    @Test
    public void direct_overrides_metaMeta_2() throws Exception {

        class SomeDomainObject {
            public void updateName(
                @MetaPublished
                @NotPublished
                @DomainObj(publishng = DomainObj.Publishng.YES)
                    String name
            ) {}
        }

        Method method = SomeDomainObject.class.getMethod("updateName", String.class);
        final List<DomainObj> annotations = Annotations.getAnnotations(method, 0, DomainObj.class);

        Assert.assertThat(annotations.size(), is(3));

        Assert.assertThat(annotations.get(0).publishng(), is(DomainObj.Publishng.YES));
        Assert.assertThat(annotations.get(1).publishng(), is(DomainObj.Publishng.NO));
        Assert.assertThat(annotations.get(2).publishng(), is(DomainObj.Publishng.YES));
    }

}