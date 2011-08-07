/* Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.activiti.cdi.impl.util;

import java.util.Iterator;

import javax.enterprise.context.spi.CreationalContext;
import javax.enterprise.inject.spi.Bean;
import javax.enterprise.inject.spi.BeanManager;

/**
 * Utility class for performing programmatic bean lookups.
 * 
 * @author Daniel Meyer
 */
public class ProgrammaticBeanLookup {

  @SuppressWarnings("unchecked")
  public static <T> T lookup(Class<T> clazz) {
    BeanManager bm = BeanManagerLookup.getBeanManager();
    Iterator<Bean< ? >> iter = bm.getBeans(clazz).iterator();
    if (!iter.hasNext()) {
      throw new IllegalStateException("CDI BeanManager cannot find an instance of requested type " + clazz.getName());
    }
    Bean<T> bean = (Bean<T>) iter.next();
    CreationalContext<T> ctx = bm.createCreationalContext(bean);
    T dao = (T) bm.getReference(bean, clazz, ctx);
    return dao;
  }

  @SuppressWarnings({ "unchecked", "rawtypes" })
  public static Object lookup(String name) {
    BeanManager bm = BeanManagerLookup.getBeanManager();
    Iterator<Bean< ? >> iter = bm.getBeans(name).iterator();
    if (!iter.hasNext()) {
      throw new IllegalStateException("CDI BeanManager cannot find an instance of requested type '" + name + "'");
    }
    Bean bean = iter.next();
    CreationalContext ctx = bm.createCreationalContext(bean);
    return bm.getReference(bean, bean.getBeanClass(), ctx);
  }

}
