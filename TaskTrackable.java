package org.cet.cs28.trackit.service.entity;

import org.cet.cs28.trackit.service.utils.DBConnectionManager;
import org.cet.cs28.trackit.service.utils.OperationStatus;
import org.cet.cs28.trackit.service.utils.Priority;
import org.cet.cs28.trackit.service.utils.TrackableStatus;
import org.cet.cs28.trackit.ui.view.renderer.TrackableRenderer;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class TaskTrackable extends Trackable {

    private String task;
    private String frequency;
    private String description;

    private static final String INSERT_TASK_SQL = "INSERT INTO class_task "
            +"(dueDate, priority, task, description, status, frequency)"
            +"VALUES(?, ?, ?, ?, ?, ?)";

    private static final String DELETE_TASK_SQL ="DELETE FROM class_task"
            +" WHERE id=?";

    private static final String GET_ALL_TASK_SQL = "SELECT * FROM class_task";

    private static final String UPDATE_TASK_SQL = "UPDATE class_task"
            +" SET dueDate=?, priority=?, task=?, description=?, status=?, frequency=? "
            +"WHERE id=?";

    public TaskTrackable(){

    }

    public TaskTrackable(int id, Timestamp dueDate, TrackableStatus status, Priority priority, String task, String description, String frequency ){
        super.setId(id);
        super.setDueDate(dueDate);
        super.setStatus(status);
        super.setPriority(priority);
        this.task=task;
        this.description=description;
        this.frequency=frequency;
    }

    @Override
    public TrackableRenderer createRenderer() {
        return null;
    }

    @Override
    public OperationStatus save() {
        DBConnectionManager dbConnManager = new DBConnectionManager();
        Connection Conn= dbConnManager.getConnection();

        if(Conn==null){
            return OperationStatus.FAILURE;
        }

        try{
            PreparedStatement preparedStatement= Conn.prepareStatement(INSERT_TASK_SQL, Statement.RETURN_GENERATED_KEYS);
            preparedStatement.setTimestamp(1, getDueDate());
            preparedStatement.setString(2, getPriority().toString());
            preparedStatement.setString(3, getTask());
            preparedStatement.setString(4, getDescription());
            preparedStatement.setString(5, getStatus().toString());
            preparedStatement.setString(6, getFrequency());


        preparedStatement.executeUpdate();

        ResultSet generatedKeys = preparedStatement.getGeneratedKeys();
        if (generatedKeys.next()) {
            int generatedId = generatedKeys.getInt(1);
            super.setId(generatedId); // Set the generated ID to the Trackable
        } else {
            System.err.println("Creating task failed, no ID obtained.");
            return OperationStatus.FAILURE;
        }
        Conn.commit();
        preparedStatement.close();
        dbConnManager.closeConnection();
        return OperationStatus.SUCCESS;

    } catch (SQLException e) {
        e.printStackTrace();
        return OperationStatus.FAILURE;
        }

    }


    @Override
    public OperationStatus edit() {
        DBConnectionManager dbConnManager = new DBConnectionManager();
        Connection conn = dbConnManager.getConnection();

        if (conn == null) {
            return OperationStatus.FAILURE;
        }

        try {
            PreparedStatement preparedStatement = conn.prepareStatement(UPDATE_TASK_SQL);
            preparedStatement.setTimestamp(1, getDueDate());
            preparedStatement.setString(2, getPriority().toString());
            preparedStatement.setString(3, getTask());
            preparedStatement.setString(4, getDescription());
            preparedStatement.setString(5, getStatus().toString());
            preparedStatement.setString(6, getFrequency());
            preparedStatement.setInt(7, getId());

            preparedStatement.executeUpdate();
            conn.commit();
            preparedStatement.close();
            dbConnManager.closeConnection();
            return OperationStatus.SUCCESS;

        } catch (SQLException e) {
            e.printStackTrace();
            return OperationStatus.FAILURE;
        }
    }

    @Override
    public OperationStatus delete() {
        DBConnectionManager dbConnManager = new DBConnectionManager();
        Connection conn = dbConnManager.getConnection();

        if (conn == null) {
            return OperationStatus.FAILURE;
        }

        try {
            PreparedStatement preparedStatement = conn.prepareStatement(DELETE_TASK_SQL);
            preparedStatement.setInt(1, getId());

            preparedStatement.executeUpdate();
            conn.commit();
            preparedStatement.close();
            dbConnManager.closeConnection();
            return OperationStatus.SUCCESS;

        } catch (SQLException e) {
            e.printStackTrace();
            return OperationStatus.FAILURE;
        }
    }


    @Override
    public List<Trackable> getAll() {

        List<Trackable> tasks = new ArrayList<>();

        DBConnectionManager dbConnManager = new DBConnectionManager();
        Connection conn = dbConnManager.getConnection();

        if (conn == null) {
            return tasks;
        }

        try {
            PreparedStatement preparedStatement = conn.prepareStatement(GET_ALL_TASK_SQL);
            ResultSet resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                int id = resultSet.getInt("id");
                Timestamp dueDate = resultSet.getTimestamp("duedate");
                String priorityStr = resultSet.getString("priority");
                String title = resultSet.getString("task");
                String description = resultSet.getString("description");
                String statusStr = resultSet.getString("status");
                String frequency = resultSet.getString("frequency");

                TrackableStatus status = TrackableStatus.valueOf(statusStr);
                Priority priority = Priority.valueOf(priorityStr);

                TaskTrackable task = new TaskTrackable(id, dueDate, status, priority, title,
                        description, frequency);
                tasks.add(task);
            }

            preparedStatement.close();
            dbConnManager.closeConnection();
            return tasks;

        } catch (SQLException e) {
            e.printStackTrace();
            return tasks;
        }
    }
    //return task name
    public String getTask(){return this.task;}

    //set task name
    public void setTask(String task){this.task=task;}

    //return task description
    public String getDescription(){return this.description;}

    //set task description
    public void setDescription(String description){this.description=description;}

    //return task frequency
    public String getFrequency(){return this.frequency;}

    //set task frequency
    public void setFrequency(String frequency){this.frequency=frequency;}
}
