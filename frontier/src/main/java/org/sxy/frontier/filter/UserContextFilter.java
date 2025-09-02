package org.sxy.frontier.filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.sxy.frontier.dto.UserPrinciple;
import org.sxy.frontier.utility.UserContextHolder;

import java.io.IOException;

@Component
public class UserContextFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        try {
            extractUserFromHeaders(request);
            filterChain.doFilter(request, response);
        } finally {
            UserContextHolder.clear();
        }
    }

    private void extractUserFromHeaders(HttpServletRequest request){
        String userId=request.getHeader("X-USER-ID");
        if(userId==null)
            return;
        String userName=request.getHeader("X-USER-NAME");
        String email=request.getHeader("X-EMAIL");
        UserPrinciple principal=new UserPrinciple(email,userName,userId);
        UserContextHolder.setUser(principal);
    }
}
