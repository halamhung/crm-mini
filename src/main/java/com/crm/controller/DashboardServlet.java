package com.crm.controller;

import com.crm.dao.StatisticDAO;
import com.crm.model.Statistic;
import com.crm.model.User;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

@WebServlet("")
public class DashboardServlet extends BaseServlet {
    private static final long serialVersionUID = 1L;

    private StatisticDAO statisticDAO = new StatisticDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        try {
            User user = currentUser(request);
            List<Statistic> projectStats = statisticDAO.findByProject(user);
            List<Statistic> employeeStats = statisticDAO.findByEmployee(user);
            Statistic summary = new Statistic();
            for (Statistic statistic : projectStats) {
                summary.setTotal(summary.getTotal() + statistic.getTotal());
                summary.setPending(summary.getPending() + statistic.getPending());
                summary.setActive(summary.getActive() + statistic.getActive());
                summary.setDone(summary.getDone() + statistic.getDone());
            }
            request.setAttribute("summary", summary);
            request.setAttribute("projectStats", projectStats);
            request.setAttribute("employeeStats", employeeStats);
            forward(request, response, "dashboard");
        } catch (Exception e) {
            showError(request, response, e);
        }
    }
}
