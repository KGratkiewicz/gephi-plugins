package Random.N;/*
Copyright 2008-2011 Gephi
Authors : Mathieu Bastian <mathieu.bastian@gephi.org>
Website : http://www.gephi.org

This file is part of Gephi.

DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.

Copyright 2011 Gephi Consortium. All rights reserved.

The contents of this file are subject to the terms of either the GNU
General Public License Version 3 only ("GPL") or the Common
Development and Distribution License("CDDL") (collectively, the
"License"). You may not use this file except in compliance with the
License. You can obtain a copy of the License at
http://gephi.org/about/legal/license-notice/
or /cddl-1.0.txt and /gpl-3.0.txt. See the License for the
specific language governing permissions and limitations under the
License.  When distributing the software, include this License Header
Notice in each file and include the License files at
/cddl-1.0.txt and /gpl-3.0.txt. If applicable, add the following below the
License Header, with the fields enclosed by brackets [] replaced by
your own identifying information:
"Portions Copyrighted [year] [name of copyright owner]"

If you wish your version of this file to be governed by only the CDDL
or only the GPL Version 3, indicate your decision by adding
"[Contributor] elects to include this software in this distribution
under the [CDDL or GPL Version 3] license." If you do not indicate a
single choice of license, a recipient has the option to distribute
your version of this file under either the CDDL, the GPL Version 3 or
to extend the choice of license to its licensees as provided above.
However, if you add GPL Version 3 code and therefore, elected the GPL
Version 3 license, then the option applies only if the new code is
made subject to such option by the copyright holder.

Contributor(s):

Portions Copyrighted 2011 Gephi Consortium.
 */

import javax.swing.Icon;
import javax.swing.JPanel;

import org.gephi.filters.spi.Category;
import org.gephi.filters.spi.Filter;
import org.gephi.filters.spi.FilterBuilder;
import org.gephi.project.api.Workspace;
import org.openide.util.lookup.ServiceProvider;

/**
 * Filter builder for the {@link } filter.
 * <p>
 * This class configures how the filter should be integrated. It specifies it
 * belongs to the edge category, the name, icon and description.
 * <p>
 * This example doesn't have any user interface so the <code>getPanel()</code>
 * returns null.
 *
 * @author Mathieu Bastian
 */
@ServiceProvider(service = FilterBuilder.class)
public class RandomNRemovalStrategyBuilder implements FilterBuilder {

    @Override
    public Category getCategory() {
        return new Category("RemovalStrategy");
    }

    @Override
    public String getName() {
        return "Random - N version";
    }

    @Override
    public Icon getIcon() {
        return null;
    }

    @Override
    public String getDescription() {
        return "Random remove N nodes. Seed - seed of random generator";
    }

    @Override
    public Filter getFilter(Workspace workspace) {
        return new RandomNRemovalStrategy();
    }

    @Override
    public JPanel getPanel(Filter filter) {
        RandomNRemovalStrategyParams panel = new RandomNRemovalStrategyParams(filter);
        return panel;
    }

        @Override
    public void destroy(Filter filter) {
    }
}