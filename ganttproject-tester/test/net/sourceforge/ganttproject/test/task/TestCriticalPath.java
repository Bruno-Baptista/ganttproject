/*
GanttProject is an opensource project management tool.
Copyright (C) 2009 Dmitry Barashev

This program is free software; you can redistribute it and/or
modify it under the terms of the GNU General Public License
as published by the Free Software Foundation; either version 2
of the License, or (at your option) any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with this program; if not, write to the Free Software
Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
*/
package net.sourceforge.ganttproject.test.task;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import biz.ganttproject.core.calendar.WeekendCalendarImpl;
import biz.ganttproject.core.time.GanttCalendar;
import biz.ganttproject.core.time.CalendarFactory;
import net.sourceforge.ganttproject.TestSetupHelper;
import net.sourceforge.ganttproject.task.Task;
import net.sourceforge.ganttproject.task.TaskManager;
import net.sourceforge.ganttproject.task.TaskMutator;
import net.sourceforge.ganttproject.task.dependency.TaskDependency;
import net.sourceforge.ganttproject.task.dependency.TaskDependencyConstraint;
import net.sourceforge.ganttproject.task.dependency.constraint.FinishFinishConstraintImpl;
import net.sourceforge.ganttproject.task.dependency.constraint.StartStartConstraintImpl;


public class TestCriticalPath extends TaskTestCase {
    public void testSinglePathIsCritical() throws Exception {
        TaskManager mgr = getTaskManager();
        Task t1 = createTask();
        Task t2 = createTask();
        Task t3 = createTask();
        createDependency(t3, t2);
        createDependency(t2, t1);
        Set<Task> criticalTasks = new HashSet<Task>(Arrays.asList(
                mgr.getAlgorithmCollection().getCriticalPathAlgorithm().getCriticalTasks()));
        assertTrue(criticalTasks.contains(t1));
        assertTrue(criticalTasks.contains(t2));
        assertTrue(criticalTasks.contains(t3));
    }

    public void testLongerPathIsCritical() throws Exception {
        TaskManager mgr = getTaskManager();
        Task t1 = createTask();
        Task t2 = createTask();
        Task t3 = createTask();
        Task t4 = createTask();
        Task t5 = createTask();
        createDependency(t4, t3);
        createDependency(t3, t2);
        createDependency(t2, t1);
        createDependency(t4, t5);
        createDependency(t5, t1);
        Set<Task> criticalTasks = new HashSet<Task>(Arrays.asList(
                mgr.getAlgorithmCollection().getCriticalPathAlgorithm().getCriticalTasks()));
        assertTrue(criticalTasks.contains(t1));
        assertTrue(criticalTasks.contains(t2));
        assertTrue(criticalTasks.contains(t3));
        assertTrue(criticalTasks.contains(t4));
        assertFalse(criticalTasks.contains(t5));
    }

    public void testEqualPathsAreBothCritical() throws Exception {
        TaskManager mgr = getTaskManager();
        Task t1 = createTask();
        Task t2 = createTask();
        Task t3 = createTask();
        Task t4 = createTask();
        createDependency(t4, t2);
        createDependency(t4, t3);
        createDependency(t2, t1);
        createDependency(t3, t1);

        Set<Task> criticalTasks = new HashSet<Task>(Arrays.asList(
                mgr.getAlgorithmCollection().getCriticalPathAlgorithm().getCriticalTasks()));
        assertTrue(criticalTasks.contains(t1));
        assertTrue(criticalTasks.contains(t2));
        assertTrue(criticalTasks.contains(t3));
        assertTrue(criticalTasks.contains(t4));
    }

    public void testGetTaskSlack() {
        TaskManager withAlwaysWorkingCalendar = TestSetupHelper.newTaskManagerBuilder().build();
        TaskManager withWeekendCalendar = TestSetupHelper.newTaskManagerBuilder().withCalendar(new WeekendCalendarImpl()).build();

        Task t1 = createTask();
        GanttCalendar start = t1.getStart();
        Task t2 = createTask();
        Task t3 = createTask();
        Task t4 = createTask();
        createDependency(t4, t2);
        createDependency(t4, t3);
        createDependency(t2, t1);
        createDependency(t3, t1);

        int slack1 = withAlwaysWorkingCalendar.getAlgorithmCollection().getCriticalPathAlgorithm().getTaskSlack(t1);
        int slack2 = withAlwaysWorkingCalendar.getAlgorithmCollection().getCriticalPathAlgorithm().getTaskSlack(t2);
        int slack3 = withAlwaysWorkingCalendar.getAlgorithmCollection().getCriticalPathAlgorithm().getTaskSlack(t3);
        int slack4 = withAlwaysWorkingCalendar.getAlgorithmCollection().getCriticalPathAlgorithm().getTaskSlack(t4);
        // Verifies that these tasks are in the critical path (slack = 0)
        assertEquals(0, slack1);
        assertEquals(0, slack2);
        assertEquals(0, slack3);
        assertEquals(0, slack4);

        Task t5 = createTask();
        Task t6 = createTask();
        Task t7 = createTask();
        Task t8 = createTask();
        Task t9 = createTask();
        createDependency(t9, t8);
        createDependency(t8, t7);
        createDependency(t7, t5);
        createDependency(t6, t5);

        slack1 = withAlwaysWorkingCalendar.getAlgorithmCollection().getCriticalPathAlgorithm().getTaskSlack(t1);
        slack2 = withAlwaysWorkingCalendar.getAlgorithmCollection().getCriticalPathAlgorithm().getTaskSlack(t2);
        slack3 = withAlwaysWorkingCalendar.getAlgorithmCollection().getCriticalPathAlgorithm().getTaskSlack(t3);
        slack4 = withAlwaysWorkingCalendar.getAlgorithmCollection().getCriticalPathAlgorithm().getTaskSlack(t4);

        int slack5 = withAlwaysWorkingCalendar.getAlgorithmCollection().getCriticalPathAlgorithm().getTaskSlack(t5);
        int slack6 = withAlwaysWorkingCalendar.getAlgorithmCollection().getCriticalPathAlgorithm().getTaskSlack(t6);
        int slack7 = withAlwaysWorkingCalendar.getAlgorithmCollection().getCriticalPathAlgorithm().getTaskSlack(t7);
        int slack8 = withAlwaysWorkingCalendar.getAlgorithmCollection().getCriticalPathAlgorithm().getTaskSlack(t8);
        int slack9 = withAlwaysWorkingCalendar.getAlgorithmCollection().getCriticalPathAlgorithm().getTaskSlack(t9);


        /*
        Verifies that these tasks are not in the critical path anymore,
        in total they take 3 days to complete minimum, while the new task is in the critical path
         */
        assertEquals(1, slack1);
        assertEquals(1, slack2);
        assertEquals(1, slack3);
        assertEquals(1, slack4);
        assertEquals(0, slack5);
        assertEquals(0, slack5);
        assertEquals(0, slack6);
        assertEquals(0, slack7);
        assertEquals(0, slack8);
        assertEquals(0, slack9);

        t9.delete();
        assertEquals(0, slack1);
        assertEquals(0, slack2);
        //assertEquals(0 , sla);


    }

    public void testUnlinkedTaskICriticalIfEndsAtTheProjectEnd() throws Exception {
        TaskManager mgr = getTaskManager();
        Task t1 = createTask();
        Task t2 = createTask();
        Task t3 = createTask();
        createDependency(t3, t2);
        createDependency(t2, t1);

        Task u1 = createTask();
        TaskMutator mu1 = u1.createMutatorFixingDuration();
        mu1.setStart(t3.getStart());
        mu1.commit();

        Task u2 = createTask();
        TaskMutator mu2 = u2.createMutatorFixingDuration();
        mu2.setStart(t2.getStart());
        mu2.commit();

        Set<Task> criticalTasks = new HashSet<Task>(Arrays.asList(
                mgr.getAlgorithmCollection().getCriticalPathAlgorithm().getCriticalTasks()));
        assertTrue(criticalTasks.contains(t1));
        assertTrue(criticalTasks.contains(t2));
        assertTrue(criticalTasks.contains(t3));
        assertTrue(criticalTasks.contains(u1));
        assertFalse(criticalTasks.contains(u2));
    }

    public void testCriticalPathInsideCriticalSupertask() throws Exception {
        Task t1 = createTask();
        Task t2 = createTask();
        Task t3 = createTask();
        createDependency(t3, t2);
        createDependency(t2, t1);

        Task n1 = createTask();
        n1.move(t2);
        Task n2 = createTask();
        n2.move(t2);
        createDependency(n2, n1);

        TaskManager mgr = getTaskManager();

        Set<Task> criticalTasks = new HashSet<Task>(Arrays.asList(
                mgr.getAlgorithmCollection().getCriticalPathAlgorithm().getCriticalTasks()));
        assertTrue(criticalTasks.contains(t1));
        assertTrue(criticalTasks.contains(t2));
        assertTrue(criticalTasks.contains(t3));

        assertTrue(criticalTasks.contains(n1));
        assertTrue(criticalTasks.contains(n2));
    }

    public void testLag() throws Exception {
        TaskManager mgr = getTaskManager();
        Task t1 = createTask();
        Task t2 = createTask();
        Task t3 = createTask();
        createDependency(t3, t2);
        TaskDependency dep = createDependency(t2, t1);
        dep.setDifference(2);

        Set<Task> criticalTasks = new HashSet<Task>(Arrays.asList(
                mgr.getAlgorithmCollection().getCriticalPathAlgorithm().getCriticalTasks()));
        assertTrue(criticalTasks.contains(t1));
        assertTrue(criticalTasks.contains(t2));
        assertTrue(criticalTasks.contains(t3));
    }

    public void testTaskBeforeNonCriticalIsNonCritical() throws Exception {
        TaskManager mgr = getTaskManager();
        Task t1 = createTask();
        Task t2 = createTask();
        Task t3 = createTask();
        Task t4 = createTask();
        Task t5 = createTask();
        Task t6 = createTask();
        createDependency(t2, t1);
        createDependency(t4, t3);
        createDependency(t5, t4);
        createDependency(t6, t5);
        createDependency(t6, t2);

        Set<Task> criticalTasks = new HashSet<Task>(Arrays.asList(
                mgr.getAlgorithmCollection().getCriticalPathAlgorithm().getCriticalTasks()));
        assertTrue(criticalTasks.contains(t3));
        assertTrue(criticalTasks.contains(t4));
        assertTrue(criticalTasks.contains(t5));
        assertTrue(criticalTasks.contains(t6));
        assertFalse(criticalTasks.contains(t1));
        assertFalse(criticalTasks.contains(t2));
    }

    class LaggedDependencyChainCriticalPathTester {
        private final Task t1;
        private final Task t2;
        private final Task t3;
        private final HashSet<Task> criticalTasks;

        public LaggedDependencyChainCriticalPathTester(TaskDependencyConstraint constraint) throws Exception {
            TaskManager mgr = getTaskManager();
            t1 = createTask();
            t2 = createTask();
            t3 = createTask();
            TaskDependency dep_3_2 = createDependency(t3, t2);
            dep_3_2.setConstraint((TaskDependencyConstraint) constraint.clone());

            TaskDependency dep_2_1 = createDependency(t2, t1);
            dep_2_1.setConstraint((TaskDependencyConstraint) constraint.clone());
            dep_2_1.setDifference(2);

            mgr.getAlgorithmCollection().getRecalculateTaskScheduleAlgorithm().run();
            criticalTasks = new HashSet<Task>(Arrays.asList(
                    mgr.getAlgorithmCollection().getCriticalPathAlgorithm().getCriticalTasks()));
        }

        public void runDefaultAsserts() {
            assertTrue(criticalTasks.contains(t1));
            assertTrue(criticalTasks.contains(t2));
            assertTrue(criticalTasks.contains(t3));
        }
    }

    public void testFinishFinishConstraint() throws Exception {
        LaggedDependencyChainCriticalPathTester tester =
            new LaggedDependencyChainCriticalPathTester(new FinishFinishConstraintImpl());
        tester.runDefaultAsserts();
    }

    public void testStartStartConstraint() throws Exception {
        LaggedDependencyChainCriticalPathTester tester =
            new LaggedDependencyChainCriticalPathTester(new StartStartConstraintImpl());
        tester.runDefaultAsserts();
    }

    public void testStartFinishConstraint() throws Exception {
        LaggedDependencyChainCriticalPathTester tester =
            new LaggedDependencyChainCriticalPathTester(new StartStartConstraintImpl());
        assertTrue(tester.criticalTasks.contains(tester.t1));
        assertTrue(tester.criticalTasks.contains(tester.t2));
    }

}
